package intermediate.code;

/**
 * 无条件跳转
 */
public class Jump extends ILinkNode {

    private final BasicBlock target;

    public Jump(BasicBlock target) {
        this.target = target;
    }

    public BasicBlock getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "J " + target.getLabel();
    }
}
