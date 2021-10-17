package intermediate.code;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

/**
 * 一元运算的中间代码
 */
public class Unary extends IntermediateCode implements InOrder {
    private IntermediateCode next;

    @Override
    public IntermediateCode getNext() {
        return next;
    }

    public void setNext(IntermediateCode next) {
        this.next = next;
    }

    public enum Op {
        MOV,
        NEG,
        NOT
    }

    private final Op op;
    private final Operand src;
    private final Symbol dst;

    public Unary(Op op,
                 Operand src, Symbol dst,
                 String function, String block, int index) {
        super(function, block, index);
        this.op = op;
        this.src = src;
        this.dst = dst;
    }

    public Op getOp() {
        return op;
    }

    public Operand getSrc() {
        return src;
    }

    public Symbol getDst() {
        return dst;
    }

    @Override
    public String getName() {
        return "UNARY_" + op.name();
    }

    @Override
    public String toString() {
        return getName() + " " + src + " -> " + dst + " ----> " + next.getLabel();
    }
}
