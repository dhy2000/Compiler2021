package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class BranchNotEqual extends MipsInstruction {

    private final int regSrc1;
    private final int regSrc2;
    private final String target;

    public BranchNotEqual(int regSrc1, int regSrc2, String target) {
        this.regSrc1 = regSrc1;
        this.regSrc2 = regSrc2;
        assert !target.isEmpty();
        this.target = target;
    }

    public int getRegSrc1() {
        return regSrc1;
    }

    public int getRegSrc2() {
        return regSrc2;
    }

    @Override
    public String instrToString() {
        return String.format("bne $%s, $%s, %s", RegisterFile.getRegisterName(regSrc1), RegisterFile.getRegisterName(regSrc2), target);
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {

    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return rf.read(regSrc1) != rf.read(regSrc2);
    }

    @Override
    public String getJumpTarget() {
        return target;
    }
}
