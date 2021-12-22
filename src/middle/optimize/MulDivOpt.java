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

    public MulDivOpt() {

    }

    @Override
    public void optimize(MiddleCode ir) {
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
                        if (src1 instanceof Symbol && src2 instanceof Symbol) {
                            node = node.getNext();
                            continue;
                        }
                        if (op.equals(BinaryOp.Op.MUL)) {
                            // 变量 * 立即数, 立即数是 2 的幂
                            // 双立即数的可以忽略掉
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
                            if (src1 instanceof Symbol && src2 instanceof Immediate) {
                                if (MathUtil.isLog2(((Immediate) src2).getValue())) {
                                    if (ADVANCED_DIV_OPT) {
                                        // TODO: 完整的除法优化
                                        node = node.getNext();
                                    } else {
                                        // 简易版除法优化，仅适用于除数为 2 的幂
                                        Immediate operand2 = new Immediate(MathUtil.log2(((Immediate) src2).getValue()));
                                        final String field = "div_opt";
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
                                    }
                                    continue;
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
