package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

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
    public void execute(RegisterFile rf, Memory mem) {
        int pc = rf.getProgramCounter();
        rf.write(RegisterFile.Register.RA, pc + 4);
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
