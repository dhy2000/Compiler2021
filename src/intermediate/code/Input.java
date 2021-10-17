package intermediate.code;

import intermediate.operand.Symbol;

/**
 * 输入语句
 */
public class Input extends IntermediateCode implements InOrder {

    private final Symbol dst;

    public Input(
            Symbol dst,
            String function, String block, int index) {
        super(function, block, index);
        this.dst = dst;
    }

    public Symbol getDst() {
        return dst;
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
        return "INPUT";
    }

    @Override
    public String toString() {
        return getName() + " " + dst;
    }
}
