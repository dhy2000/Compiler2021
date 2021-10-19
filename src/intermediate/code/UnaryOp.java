package intermediate.code;

import intermediate.operand.Operand;
import intermediate.symbol.Symbol;

/**
 * 一元运算
 */
public class UnaryOp extends ILinkNode {
    public enum Op {
        MOV,
        NEG,
        NOT
    }

    private final Op op;
    private final Operand src;
    private final Symbol dst;

    public UnaryOp(Op op, Operand src, Symbol dst) {
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
    public String toString() {
        return "UNARY." + op.name() + " " + src + ", " + dst;
    }
}
