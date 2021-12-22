package backend.instruction;

import backend.hardware.RegisterFile;

public class AbsoluteValue extends MipsInstruction {
    private final int regDst;
    private final int regSrc;

    public AbsoluteValue(int regDst, int regSrc) {
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
        return String.format("abs $%s, $%s", RegisterFile.getRegisterName(regDst), RegisterFile.getRegisterName(regSrc));
    }
}
