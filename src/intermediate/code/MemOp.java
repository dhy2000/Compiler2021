package intermediate.code;

import intermediate.operand.Operand;
import intermediate.symbol.Symbol;

/**
 * 对指针的读写操作
 */
public class MemOp extends ILinkNode {

    public enum Op {
        LOAD,
        STORE
    }

    private final Op op;
    private final Operand address;
    private final Operand value;

    public MemOp(Op op, Operand address, Operand value) {
        this.op = op;
        this.address = address;
        this.value = value;
    }

    public Op getOp() {
        return op;
    }

    public Operand getAddress() {
        return address;
    }

    public Operand getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MEM." + op.name() + " " + value + " [" + address + "]";
    }
}
