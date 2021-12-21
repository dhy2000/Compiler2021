package backend.instruction;

import backend.hardware.RegisterFile;

public class StoreWord extends MipsInstruction {

    private final int regBase;
    private final int offset;
    private final int regSrc;

    public StoreWord(int regBase, int offset, int regSrc) {
        this.regBase = regBase;
        this.offset = offset;
        this.regSrc = regSrc;
    }

    public int getRegBase() {
        return regBase;
    }

    public int getOffset() {
        return offset;
    }

    public int getRegSrc() {
        return regSrc;
    }

    @Override
    public String instrToString() {
        return String.format("sw $%s, %d($%s)", RegisterFile.getRegisterName(regSrc), offset, RegisterFile.getRegisterName(regBase));
    }
}
