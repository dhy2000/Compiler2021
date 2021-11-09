package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class ShiftRightArithmetic extends MipsInstruction {

    private final int regDst;
    private final int regSrc;
    private final int bits;

    public ShiftRightArithmetic(int regDst, int regSrc, int bits) {
        this.regDst = regDst;
        this.regSrc = regSrc;
        this.bits = bits;
    }

    public int getRegDst() {
        return regDst;
    }

    public int getRegSrc() {
        return regSrc;
    }

    public int getBits() {
        return bits;
    }

    @Override
    public String instrToString() {
        return String.format("sra $%s, $%s, %d", RegisterFile.getRegisterName(regDst), RegisterFile.getRegisterName(regSrc), bits);
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        rf.write(regDst, rf.read(regSrc) >> bits);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
