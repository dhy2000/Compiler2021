package intermediate.code.old;

import intermediate.operand.Operand;

public class BranchIf extends IntermediateCode implements Branch {

    private final Operand condition;
    private final IntermediateCode thenTarget;
    private final IntermediateCode elseTarget;

    public BranchIf(
            Operand condition, IntermediateCode thenTarget, IntermediateCode elseTarget,
            String function, String block, int index) {
        super(function, block, index);
        this.condition = condition;
        this.thenTarget = thenTarget;
        this.elseTarget = elseTarget;
    }

    public Operand getCondition() {
        return condition;
    }

    @Override
    public IntermediateCode getThen() {
        return thenTarget;
    }

    @Override
    public IntermediateCode getElse() {
        return elseTarget;
    }

    @Override
    public String getName() {
        return "BRANCH";
    }

    @Override
    public String toString() {
        return getName() + " " + condition + " ? " + thenTarget.getLabel() + " : " + elseTarget.getLabel();
    }
}
