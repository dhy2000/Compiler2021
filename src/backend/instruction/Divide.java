package backend.instruction;

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
}
