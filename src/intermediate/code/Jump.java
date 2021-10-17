package intermediate.code;

/**
 * 无条件直接跳转
 */
public class Jump extends IntermediateCode implements InOrder {

    private final IntermediateCode target;

    public Jump(
            IntermediateCode target,
            String function, String block, int index) {
        super(function, block, index);
        this.target = target;
    }

    @Override
    public IntermediateCode getNext() {
        return target;
    }

    @Override
    public String getName() {
        return "JUMP";
    }

    @Override
    public String toString() {
        return getName() + " " + target.getLabel();
    }
}
