package intermediate.code;

import intermediate.operand.Operand;
import intermediate.symbol.Symbol;

/**
 * 指针相关的逻辑
 */
public class PointerOp extends ILinkNode {
    public enum Op {
        LOAD,
        STORE
    }

    private final Op op;
    private final Symbol address;
    private final Symbol dst;
    private final Operand src;

    public PointerOp(Op op, Symbol address, Operand another) {
        this.op = op;
        this.address = address;
        assert address.getType().equals(Symbol.Type.POINTER);
        if (op.equals(Op.LOAD)) {
            assert another instanceof Symbol;
            this.dst = (Symbol) another;
            this.src = null;
        } else {
            assert op.equals(Op.STORE);
            this.dst = null;
            this.src = another;
        }
    }

    public Op getOp() {
        return op;
    }

    public Symbol getAddress() {
        return address;
    }

    public Symbol getDst() {
        assert op.equals(Op.LOAD);
        return dst;
    }

    public Operand getSrc() {
        assert op.equals(Op.STORE);
        return src;
    }

    @Override
    public String toString() {
        return op.name() + " " + address + ", " + (op.equals(Op.LOAD) ? getDst() : getSrc());
    }
}
