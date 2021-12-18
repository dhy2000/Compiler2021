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
 * 将 2 的整数次幂的乘除法换成移位
 */
public class MulDivToShift implements MidOptimizer {

    public MulDivToShift() {

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
                            // 负数的除法简化成位运算有 BUG！
                            if (src1 instanceof Symbol && src2 instanceof Immediate) {
                                if (MathUtil.isLog2(((Immediate) src2).getValue())) {
                                    Immediate operand2 = new Immediate(MathUtil.log2(((Immediate) src2).getValue()));
                                    node.insertAfter(new BinaryOp(BinaryOp.Op.SRA, src1, operand2, code.getDst()));
                                    node.remove();
                                    node = node.getNext();
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
