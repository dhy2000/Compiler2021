package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class JumpAndLink extends MipsInstruction {

    public JumpAndLink(String label) {
        setLabel(label);
    }

    @Override
    public String instrToString() {
        return null;
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {

    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
