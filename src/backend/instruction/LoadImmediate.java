package backend.instruction;

import backend.hardware.RegisterFile;

public class LoadImmediate extends MipsInstruction {

    private final int regDst;
    private final int immediate;

    public LoadImmediate(int regDst, int immediate) {
        this.regDst = regDst;
        this.immediate = immediate;
    }

    public int getRegDst() {
        return regDst;
    }

    public int getImmediate() {
        return immediate;
    }

    @Override
    public String instrToString() {
        return String.format("li $%s, %d", RegisterFile.getRegisterName(regDst), immediate);
    }
}
