package intermediate.code;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

/**
 * 数组的存入，base(ARRAY) + offset <- src
 */
public class Store extends IntermediateCode implements InOrder {

    private final Symbol base;
    private final Operand offset;
    private final Operand src;

    public Store(
            Symbol base, Operand offset, Operand src,
            String function, String block, int index) {
        super(function, block, index);
        this.base = base;
        this.offset = offset;
        this.src = src;
    }

    private IntermediateCode next;

    public Symbol getBase() {
        return base;
    }

    public Operand getOffset() {
        return offset;
    }

    public Operand getSrc() {
        return src;
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
        return "STORE";
    }

    @Override
    public String toString() {
        return getName() + " " + src + " -> " + base + " " + offset + " ----> " + next.getLabel();
    }
}
