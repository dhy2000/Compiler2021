package intermediate.code;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

public class ArrayOp {

    public enum Op {
        LOAD,
        STORE
    }

    private final Op op;
    private final Symbol base;
    private final Operand offset;
    private final Operand var;

    public ArrayOp(Op op, Symbol base, Operand offset, Operand var) {
        this.op = op;
        this.base = base;
        this.offset = offset;
        this.var = var;
    }

    public Op getOp() {
        return op;
    }

    public Operand getOffset() {
        return offset;
    }

    public Symbol getBase() {
        return base;
    }

    public Operand getVar() {
        return var;
    }

    @Override
    public String toString() {
        return "ARRAY." + op.name() + " " + var + ", " + base + "(" + offset + ")";
    }
}
