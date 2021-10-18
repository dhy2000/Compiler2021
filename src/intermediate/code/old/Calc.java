package intermediate.code.old;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

/**
 * 算术运算类的中间代码
 */
public class Calc extends IntermediateCode implements InOrder {

    private IntermediateCode next;

    @Override
    public IntermediateCode getNext() {
        return next;
    }

    public void setNext(IntermediateCode next) {
        this.next = next;
    }

    public enum Op {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        DIV,
        MOD,
        GT,
        GE,
        LT,
        LE,
        EQ,
        NE
    }

    private final Op op;
    private final Operand src1;
    private final Operand src2;
    private final Symbol dst;

    public Calc(Op op,
                Operand src1, Operand src2, Symbol dst,
                String function, String block, int index) {
        super(function, block, index);
        this.op = op;
        this.src1 = src1;
        this.src2 = src2;
        this.dst = dst;
    }

    public Operand getSrc1() {
        return src1;
    }

    public Operand getSrc2() {
        return src2;
    }

    public Op getOp() {
        return op;
    }

    public Symbol getDst() {
        return dst;
    }

    @Override
    public String getName() {
        return "CALC_" + op.name();
    }

    @Override
    public String toString() {
        return getName() + " " + src1 + " " + src2 + " -> " + dst + " ----> " + next.getLabel();
    }
}
