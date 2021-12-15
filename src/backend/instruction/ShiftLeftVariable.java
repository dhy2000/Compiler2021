package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class ShiftLeftVariable extends MipsInstruction {
    private final int regDst;
    private final int regSrc;
    private final int regBits;

    public ShiftLeftVariable(int regSrc, int regBits, int regDst) {
        this.regDst = regDst;
        this.regSrc = regSrc;
        this.regBits = regBits;
    }

    public int getRegDst() {
        return regDst;
    }

    public int getRegSrc() {
        return regSrc;
    }

    public int getRegBits() {
        return regBits;
    }

    @Override
    public String instrToString() {
        return String.format("sllv $%s, $%s, $%s", RegisterFile.getRegisterName(regDst), RegisterFile.getRegisterName(regSrc), RegisterFile.getRegisterName(regBits));
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        rf.write(regDst, rf.read(regSrc) << (rf.read(regBits) & 0x1f));
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
