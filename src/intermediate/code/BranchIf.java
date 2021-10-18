package intermediate.code;

import intermediate.operand.Operand;

/**
 * 单分支, 满足条件跳转
 */
public class BranchIf {
    private final Operand condition;
    private final BasicBlock target;

    public BranchIf(Operand condition, BasicBlock target) {
        this.condition = condition;
        this.target = target;
    }

    public Operand getCondition() {
        return condition;
    }

    public BasicBlock getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "IF " + condition + ", " + target.getLabel();
    }
}
