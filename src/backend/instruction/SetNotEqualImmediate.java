package backend.instruction;

public class SetNotEqualImmediate extends MipsInstruction {
    private final int regSrc;
    private final int immediate;
    private final int regDst;

    public SetNotEqualImmediate(int regSrc, int immediate, int regDst) {
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
        return String.format("sne $%s, $%s, %d", regDst, regSrc, immediate);
    }
}
