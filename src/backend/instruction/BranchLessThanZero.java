package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class BranchLessThanZero extends MipsInstruction {

    private final int regSrc;
    private final String target;

    public BranchLessThanZero(int regSrc, String target) {
        this.regSrc = regSrc;
        assert !target.isEmpty();
        this.target = target;
    }

    public int getRegSrc() {
        return regSrc;
    }

    @Override
    public String instrToString() {
        return String.format("bltz $%s, %s", RegisterFile.getRegisterName(regSrc), target);
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
