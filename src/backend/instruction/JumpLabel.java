package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class JumpLabel extends MipsInstruction {

    public JumpLabel(String label) {
        setLabel(label);
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
}
