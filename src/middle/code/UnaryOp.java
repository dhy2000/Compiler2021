package middle.code;

import middle.operand.Operand;
import middle.symbol.Symbol;

/**
 * 一元运算
 */
public class UnaryOp extends ILinkNode {
    public enum Op {
        MOV,    // INT to INT
        NEG,    // INT to INT
        NOT,    // INT to INT
        CLO,    // INT to INT
        CLZ,    // INT to INT
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
        return op.name() + " " + src + ", " + dst;
    }
}
