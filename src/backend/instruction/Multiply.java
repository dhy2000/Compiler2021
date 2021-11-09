package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class Multiply extends MipsInstruction {

    private final int regSrc1;
    private final int regSrc2;

    public Multiply(int regSrc1, int regSrc2) {
        this.regSrc1 = regSrc1;
        this.regSrc2 = regSrc2;
    }

    public int getRegSrc1() {
        return regSrc1;
    }

    public int getRegSrc2() {
        return regSrc2;
    }

    @Override
    public String instrToString() {
        return String.format("mult $%s, $%s", RegisterFile.getRegisterName(regSrc1), RegisterFile.getRegisterName(regSrc2));
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        long prod = (long) rf.read(regSrc1) * (long) rf.read(regSrc2);
        rf.setLo((int) (prod));
        rf.setHi((int) (prod >> 32));
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
