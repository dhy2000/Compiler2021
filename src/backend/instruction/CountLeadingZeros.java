package backend.instruction;

import backend.hardware.RegisterFile;

public class CountLeadingZeros extends MipsInstruction {
    private final int regDst;
    private final int regSrc;

    public CountLeadingZeros(int regDst, int regSrc) {
        this.regDst = regDst;
        this.regSrc = regSrc;
    }

    public int getRegDst() {
        return regDst;
    }

    public int getRegSrc() {
        return regSrc;
    }

    @Override
    public String instrToString() {
        return String.format("clz $%s, $%s", RegisterFile.getRegisterName(regDst), RegisterFile.getRegisterName(regSrc));
    }
}
