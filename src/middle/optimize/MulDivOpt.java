package middle.optimize;

import middle.MiddleCode;
import middle.code.*;
import middle.operand.Immediate;
import middle.operand.Operand;
import middle.symbol.FuncMeta;
import middle.symbol.Symbol;
import utility.MathUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * 乘除优化
 * 乘法将乘 2 的整数次幂转换成移位指令, 除以常数进行强度削弱优化
 */
public class MulDivOpt implements MidOptimizer {

    public static final boolean ADVANCED_DIV_OPT = false;
    public static final boolean OPT_MOD = true;             // 将 MOD 转化为 DIV 便于统一进行优化

    public MulDivOpt() {

    }

    private void optimizeMod(MiddleCode ir) {
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>(); // BFS
        for (FuncMeta func : ir.getFunctions().values()) {
            queue.offer(func.getBody());
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                if (visited.contains(block)) {
                    continue;
                }
                visited.add(block);
                ILinkNode node = block.getHead();
                while (Objects.nonNull(node) && node.hasNext()) {
                    detectBranch(node, queue);
                    if (node instanceof BinaryOp
                            && ((BinaryOp) node).getOp().equals(BinaryOp.Op.MOD)
                            && ((BinaryOp) node).getSrc1() instanceof Symbol
                            && ((BinaryOp) node).getSrc2() instanceof Immediate) {
                        // MOD a, b, rem --> DIV a, b, quo; MUL b, quo, part; SUB a, part, rem
                        final String field = "mod_to_div";
                        Symbol a = (Symbol) ((BinaryOp) node).getSrc1();
                        Immediate b = (Immediate) ((BinaryOp) node).getSrc2();
                        Symbol rem = ((BinaryOp) node).getDst();
                        Symbol quo = Symbol.temporary(field, Symbol.Type.INT);
                        Symbol part = Symbol.temporary(field, Symbol.Type.INT);
                        node.insertBefore(new BinaryOp(BinaryOp.Op.DIV, a, b, quo));
                        node.insertBefore(new BinaryOp(BinaryOp.Op.MUL, b, quo, part));
                        node.insertBefore(new BinaryOp(BinaryOp.Op.SUB, a, part, rem));
                        node.remove();
                    }
                    node = node.getNext();
                }
            }
        }
    }

    @Override
    public void optimize(MiddleCode ir) {
        if (OPT_MOD) {
            optimizeMod(ir);
        }
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>(); // BFS
        for (FuncMeta func : ir.getFunctions().values()) {
            queue.offer(func.getBody());
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                if (visited.contains(block)) {
                    continue;
                }
                visited.add(block);
                ILinkNode node = block.getHead();
                while (Objects.nonNull(node) && node.hasNext()) {
                    detectBranch(node, queue);
                    if (node instanceof BinaryOp) {
                        BinaryOp code = (BinaryOp) node;
                        BinaryOp.Op op = code.getOp();
                        Operand src1 = code.getSrc1();
                        Operand src2 = code.getSrc2();
                        if (op.equals(BinaryOp.Op.MUL)) {
                            // 变量 * 立即数, 立即数是 2 的幂
                            // 双立即数的可以忽略掉
                            if (src1 instanceof Immediate && src2 instanceof Immediate) {
                                node = node.getNext();
                                continue;
                            }
                            if (src1 instanceof Symbol && src2 instanceof Symbol) {
                                node = node.getNext();
                                continue;
                            }
                            if (src1 instanceof Immediate && src2 instanceof Symbol) {
                                Operand tmp = src2;
                                src2 = src1;
                                src1 = tmp;
                            }
                            if (src1 instanceof Symbol && src2 instanceof Immediate) {
                                if (MathUtil.isLog2(((Immediate) src2).getValue())) {
                                    if (((Immediate) src2).getValue() != 0) {
                                        Immediate operand2 = new Immediate(MathUtil.log2(((Immediate) src2).getValue()));
                                        node.insertAfter(new BinaryOp(BinaryOp.Op.SLL, src1, operand2, code.getDst()));
                                    } else {
                                        node.insertAfter(new UnaryOp(UnaryOp.Op.MOV, new Immediate(0), code.getDst()));
                                    }
                                    node.remove();
                                    node = node.getNext();
                                    continue;
                                }
                            }
                        } else if (op.equals(BinaryOp.Op.DIV)) {
                            // 除法简化成位运算时注意负数!
                            if (src1 instanceof Immediate && src2 instanceof Immediate) {
                                node = node.getNext();
                                continue;
                            }
                            final String field = "div_opt";

                            if (ADVANCED_DIV_OPT) {
                                /*
                                 * Reference: https://github.com/ridiculousfish/libdivide
                                 *   - struct libdivide_s32_branchfree_t
                                 *   - libdivide_s32_branchfree_gen
                                 *   - libdivide_internal_s32_gen
                                 *   - libdivide_s32_branchfree_do
                                 *
                                 * In SysY, we only consider signed-32bit division
                                 *
                                 * Use branch-free version to avoid generating new basic blocks
                                 */
                                Operand divisor = src2;

                                /* libdivide_internal_s32_gen => {magic, more} */

                                Symbol magic = Symbol.temporary(field, Symbol.Type.INT);
                                Symbol more = Symbol.temporary(field, Symbol.Type.INT);

                                /* calculate abs, log2d, and judge whether (abs & (abs - 1)) == 0 */
                                Symbol neg = Symbol.temporary(field, Symbol.Type.INT); // whether d < 0
                                Symbol abs = Symbol.temporary(field, Symbol.Type.INT);
                                Symbol clz = Symbol.temporary(field, Symbol.Type.INT);
                                Symbol log2d = Symbol.temporary(field, Symbol.Type.INT);    // 31 - clz
                                node.insertBefore(new BinaryOp(BinaryOp.Op.LT, divisor, new Immediate(0), neg)); // neg = (d < 0)
                                node.insertBefore(new UnaryOp(UnaryOp.Op.ABS, divisor, abs));
                                node.insertBefore(new UnaryOp(UnaryOp.Op.CLZ, divisor, clz));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.SUB, new Immediate(31), clz, log2d));
                                Symbol absMinusOne = Symbol.temporary(field, Symbol.Type.INT); // abs - 1
                                Symbol absAnd = Symbol.temporary(field, Symbol.Type.INT); // abs & (abs - 1)
                                node.insertBefore(new BinaryOp(BinaryOp.Op.SUB, abs, new Immediate(1), absMinusOne));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.AND, abs, absMinusOne, absAnd));
                                Symbol condPower2 = Symbol.temporary(field, Symbol.Type.INT); // (abs & (abs - 1)) == 0
                                node.insertBefore(new BinaryOp(BinaryOp.Op.EQ, absAnd, new Immediate(0), condPower2));

                                /* if (abs & (abs - 1)) == 0 then */

                                Operand magicIfPower2 = new Immediate(0);
                                // more = (d < 0) ? (log2d | 128) : (log2d)
                                Symbol extLog2d = Symbol.temporary(field, Symbol.Type.INT);
                                node.insertBefore(new BinaryOp(BinaryOp.Op.OR, log2d, new Immediate(128), extLog2d));
                                Symbol moreIfPower2 = Symbol.temporary(field, Symbol.Type.INT);
                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVN, extLog2d, neg, moreIfPower2));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVZ, log2d, neg, moreIfPower2));

                                /* else */

                                // libdivide_64_div_32_to_32
                                Symbol magicNotPower2 = Symbol.temporary(field, Symbol.Type.INT);
                                Symbol moreNotPower2 = Symbol.temporary(field, Symbol.Type.INT);





                                /* collect {magic, more} */

                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVN, magicIfPower2, condPower2, magic));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVZ, magicNotPower2, condPower2, magic));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVN, moreIfPower2, condPower2, more));
                                node.insertBefore(new BinaryOp(BinaryOp.Op.MOVZ, moreNotPower2, condPower2, more));

                                /* End of libdivide_internal_s32_gen */



                                node = node.getNext();
                                continue;
                            } else {
                                if (src1 instanceof Symbol && src2 instanceof Immediate) {
                                    if (MathUtil.isLog2(((Immediate) src2).getValue())) {
                                        // 简易版除法优化，仅适用于除数为 2 的幂
                                        Immediate operand2 = new Immediate(MathUtil.log2(((Immediate) src2).getValue()));
                                        Symbol quotient = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertAfter(new BinaryOp(BinaryOp.Op.SRA, src1, operand2, quotient));
                                        node.remove();
                                        node = node.getNext().getNext();
                                        // 被除数为负，且不能整除
                                        Symbol negFlag = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.LT, src1, new Immediate(0), negFlag));
                                        Symbol prod = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.SLL, quotient, operand2, prod));
                                        Symbol condLessZero = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.NE, src1, prod, condLessZero));
                                        Symbol cond = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.AND, negFlag, condLessZero, cond));
                                        Symbol fixQuotient = Symbol.temporary(field, Symbol.Type.INT);
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.ADD, quotient, new Immediate(1), fixQuotient));
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.MOVN, fixQuotient, cond, code.getDst()));
                                        node.insertBefore(new BinaryOp(BinaryOp.Op.MOVZ, quotient, cond, code.getDst()));
                                        continue;
                                    }
                                }
                            }
                        }
                        // 有负数, 负数对 2 的幂取模不能简化成按位与
                    }
                    node = node.getNext();
                }
            }
        }
    }
}
