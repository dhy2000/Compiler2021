package intermediate.code.old;

/**
 * 无条件直接跳转
 */
public class Jump extends IntermediateCode {

    private final IntermediateCode target;

    public Jump(
            IntermediateCode target,
            String function, String block, int index) {
        super(function, block, index);
        this.target = target;
    }

    public IntermediateCode getTarget() {
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
