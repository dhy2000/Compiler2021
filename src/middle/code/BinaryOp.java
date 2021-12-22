package middle.code;

import middle.operand.Operand;
import middle.symbol.Symbol;

/**
 * 二元运算
 */
public class BinaryOp extends ILinkNode {
    public enum Op {
        ADD,
        SUB,
        AND,
        OR,
        XOR,
        MUL,
        DIV,
        MOD,
        GT,
        GE,
        LT,
        LE,
        EQ,
        NE,
        SLL,
        SRA,
        MOVN,   // MOV src1 TO dst IF src2
        MOVZ,   // MOV src1 TO dst IF !src2
        MULHI,  // HI part of MUL
    }

    private final Op op;
    private final Operand src1;
    private final Operand src2;
    private final Symbol dst;

    public BinaryOp(Op op, Operand src1, Operand src2, Symbol dst) {
        this.op = op;
        this.src1 = src1;
        this.src2 = src2;
        this.dst = dst;
    }

    public Op getOp() {
        return op;
    }

    public Operand getSrc1() {
        return src1;
    }

    public Operand getSrc2() {
        return src2;
    }

    public Symbol getDst() {
        return dst;
    }

    @Override
    public String toString() {
        return op.name() + " " + src1 + ", " + src2 + ", " + dst;
    }
}
