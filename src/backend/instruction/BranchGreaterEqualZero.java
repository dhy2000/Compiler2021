package backend.instruction;

import backend.hardware.RegisterFile;

public class BranchGreaterEqualZero extends MipsInstruction {

    private final int regSrc;
    private final String target;

    public BranchGreaterEqualZero(int regSrc, String target) {
        this.regSrc = regSrc;
        assert !target.isEmpty();
        this.target = target;
    }

    public int getRegSrc() {
        return regSrc;
    }

    @Override
    public String instrToString() {
        return String.format("bgez $%s, %s", RegisterFile.getRegisterName(regSrc), target);
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
