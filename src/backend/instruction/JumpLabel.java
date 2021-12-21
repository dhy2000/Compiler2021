package backend.instruction;

public class JumpLabel extends MipsInstruction {

    private final String target;

    public JumpLabel(String target) {
        assert !target.isEmpty();
        this.target = target;
    }

    @Override
    public String instrToString() {
        return "j " + getJumpTarget();
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
