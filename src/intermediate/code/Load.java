package intermediate.code;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

/**
 * 数组的读出, base(ARRAY) + offset(Operand) -> Symbol
 */
public class Load extends IntermediateCode implements InOrder {

    private final Symbol base;
    private final Operand offset;
    private final Symbol dst;

    public Load(
            Symbol base, Operand offset, Symbol dst,
            String function, String block, int index) {
        super(function, block, index);
        this.base = base;
        assert base.getType().equals(Symbol.Type.ARRAY);
        this.offset = offset;
        this.dst = dst;
    }

    private IntermediateCode next;

    public Symbol getBase() {
        return base;
    }

    public Operand getOffset() {
        return offset;
    }

    public Symbol getDst() {
        return dst;
    }

    @Override
    public IntermediateCode getNext() {
        return next;
    }

    public void setNext(IntermediateCode next) {
        this.next = next;
    }

    @Override
    public String getName() {
        return "LOAD";
    }

    @Override
    public String toString() {
        return getName() + " " + base + " " + offset + " -> " + dst + " ----> " + next.getLabel();
    }
}
