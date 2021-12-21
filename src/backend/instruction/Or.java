package backend.instruction;

import backend.hardware.RegisterFile;

public class Or extends MipsInstruction {

    private final int regSrc1;
    private final int regSrc2;
    private final int regDst;

    public Or(int regSrc1, int regSrc2, int regDst) {
        this.regSrc1 = regSrc1;
        this.regSrc2 = regSrc2;
        this.regDst = regDst;
    }

    public int getRegSrc1() {
        return regSrc1;
    }

    public int getRegSrc2() {
        return regSrc2;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return String.format("or $%s, $%s, $%s",
                RegisterFile.getRegisterName(regDst),
                RegisterFile.getRegisterName(regSrc1),
                RegisterFile.getRegisterName(regSrc2));
    }
}
