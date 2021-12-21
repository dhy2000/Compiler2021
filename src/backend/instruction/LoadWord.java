package backend.instruction;

import backend.hardware.RegisterFile;

public class LoadWord extends MipsInstruction {

    private final int regBase;
    private final int offset;
    private final int regDst;

    public LoadWord(int regBase, int offset, int regDst) {
        this.regBase = regBase;
        this.offset = offset;
        this.regDst = regDst;
    }

    public int getRegBase() {
        return regBase;
    }

    public int getOffset() {
        return offset;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return String.format("lw $%s, %d($%s)", RegisterFile.getRegisterName(regDst), offset, RegisterFile.getRegisterName(regBase));
    }
}
