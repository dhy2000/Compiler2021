package intermediate.code;

import intermediate.operand.Operand;

/**
 * 双分支
 */
public class BranchIfElse extends ILinkNode {
    private final Operand condition;
    private final BasicBlock thenTarget;
    private final BasicBlock elseTarget;

    public BranchIfElse(Operand condition, BasicBlock thenTarget, BasicBlock elseTarget) {
        this.condition = condition;
        this.thenTarget = thenTarget;
        this.elseTarget = elseTarget;
    }

    public Operand getCondition() {
        return condition;
    }

    public BasicBlock getThenTarget() {
        return thenTarget;
    }

    public BasicBlock getElseTarget() {
        return elseTarget;
    }

    @Override
    public String toString() {
        return "BR " + condition + " ? " + thenTarget + " : " + elseTarget;
    }
}
