package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class MoveFromHi extends MipsInstruction {

    private final int regDst;

    public MoveFromHi(int regDst) {
        this.regDst = regDst;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return "mfhi $" + RegisterFile.getRegisterName(regDst);
    }
}
