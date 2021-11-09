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

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        int lo = rf.getLo();
        rf.write(regDst, lo);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
