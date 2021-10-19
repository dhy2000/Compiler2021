package intermediate.code;

import intermediate.operand.Operand;
import intermediate.symbol.Symbol;

/**
 * 寻址操作
 */
public class AddressOp extends ILinkNode {
    private final Symbol base; // pointer or array
    private final Operand offset;
    private final Symbol target; // pointer

    public AddressOp(Symbol base, Operand offset, Symbol target) {
        this.base = base;
        this.offset = offset;
        this.target = target;
    }

    public Symbol getBase() {
        return base;
    }

    public Operand getOffset() {
        return offset;
    }

    public Symbol getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "ADDRESS " + base + ", " + offset + ", " + target;
    }
}
