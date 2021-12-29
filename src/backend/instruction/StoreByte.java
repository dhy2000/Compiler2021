package backend.instruction;

import backend.hardware.RegisterFile;

public class StoreByte extends MipsInstruction {
    private final int regBase;
    private final int offset;
    private final int regSrc;

    public StoreByte(int regBase, int offset, int regSrc) {
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
        return String.format("sb $%s, %d($%s)", RegisterFile.getRegisterName(regSrc), offset, RegisterFile.getRegisterName(regBase));
    }
}
