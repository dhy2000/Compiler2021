package middle.code;

import middle.operand.Operand;
import middle.symbol.Symbol;

/**
 * 寻址操作
 */
public class AddressOffset extends ILinkNode {
    private final Symbol base; // pointer or array
    private final Operand offset;
    private final Symbol target; // pointer

    public AddressOffset(Symbol base, Operand offset, Symbol target) {
        assert base.getRefType().equals(Symbol.RefType.POINTER) || base.getRefType().equals(Symbol.RefType.ARRAY);
        assert target.getRefType().equals(Symbol.RefType.POINTER);
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
        return "OFFSET " + base + ", " + offset + ", " + target;
    }
}
