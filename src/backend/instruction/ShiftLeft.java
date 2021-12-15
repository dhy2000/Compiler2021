package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class ShiftLeft extends MipsInstruction {

    private final int regDst;
    private final int regSrc;
    private final int bits;

    public ShiftLeft(int regSrc, int bits, int regDst) {
        this.regDst = regDst;
        this.regSrc = regSrc;
        this.bits = bits;
    }

    public int getRegSrc() {
        return regSrc;
    }

    public int getRegDst() {
        return regDst;
    }

    public int getBits() {
        return bits;
    }

    @Override
    public String instrToString() {
        return String.format("sll $%s, $%s, %d", RegisterFile.getRegisterName(regDst), RegisterFile.getRegisterName(regSrc), bits);
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        rf.write(regDst, rf.read(regSrc) << bits);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
