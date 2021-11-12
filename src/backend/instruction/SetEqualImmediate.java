package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class SetEqualImmediate extends MipsInstruction {

    private final int regSrc;
    private final int immediate;
    private final int regDst;

    public SetEqualImmediate(int regSrc, int immediate, int regDst) {
        this.regDst = regDst;
        this.regSrc = regSrc;
        this.immediate = immediate;
    }

    public int getRegSrc() {
        return regSrc;
    }

    public int getImmediate() {
        return immediate;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return String.format("seq $%s, $%s, %d", regDst, regSrc, immediate);
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        rf.write(regDst, rf.read(regSrc) == immediate ? 1 : 0);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}