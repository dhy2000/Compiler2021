package intermediate.code;

import intermediate.operand.Symbol;

import java.util.Objects;

/**
 * 函数调用
 */
public class Call extends IntermediateCode implements InOrder {

    private final String callee;
    private final Symbol dst; // Nullable

    public Call(
            String callee,
            String function, String block, int index) {
        super(function, block, index);
        this.callee = callee;
        this.dst = null;
    }

    public Call(
            String callee, Symbol dst,
            String function, String block, int index) {
        super(function, block, index);
        this.callee = callee;
        this.dst = dst;
    }

    public String getCallee() {
        return callee;
    }

    public Symbol getDst() {
        return dst;
    }

    public boolean hasDst() {
        return Objects.nonNull(dst);
    }

    private IntermediateCode next;

    @Override
    public IntermediateCode getNext() {
        return next;
    }

    public void setNext(IntermediateCode next) {
        this.next = next;
    }

    @Override
    public String getName() {
        return "CALL";
    }

    @Override
    public String toString() {
        return getName() + " " + getCallee() + (hasDst() ? " -> " + dst : "");
    }
}
