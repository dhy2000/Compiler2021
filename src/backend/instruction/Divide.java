package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class Divide extends MipsInstruction {

    private final int regSrc1;
    private final int regSrc2;

    public Divide(int regSrc1, int regSrc2) {
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
        return String.format("div $%s, $%s", RegisterFile.getRegisterName(regSrc1), RegisterFile.getRegisterName(regSrc2));
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        int src1 = rf.read(regSrc1);
        int src2 = rf.read(regSrc2);
        int quotient = src1 / src2;
        int remainder = src1 % src2;

        rf.setHi(remainder);
        rf.setLo(quotient);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
