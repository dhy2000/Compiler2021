package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class JumpLabel extends MipsInstruction {

    private final String target;

    public JumpLabel(String target) {
        assert !target.isEmpty();
        this.target = target;
    }

    @Override
    public String instrToString() {
        return "j " + getLabel();
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {

    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return true;
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
