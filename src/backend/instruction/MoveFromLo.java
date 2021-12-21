package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class MoveFromLo extends MipsInstruction {

    private final int regDst;

    public MoveFromLo(int regDst) {
        this.regDst = regDst;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return "mflo $" + RegisterFile.getRegisterName(regDst);
    }
}
