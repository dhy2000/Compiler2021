package backend.instruction;

public class JumpAndLink extends MipsInstruction {

    private final String target;

    public JumpAndLink(String target) {
        assert !target.isEmpty();
        this.target = target;
    }

    @Override
    public String instrToString() {
        return "jal " + target;
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
