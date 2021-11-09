package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class Addu extends MipsInstruction {

    private final int regSrc1;
    private final int regSrc2;
    private final int regDst;

    public Addu(int regSrc1, int regSrc2, int regDst) {
        this.regSrc1 = regSrc1;
        this.regSrc2 = regSrc2;
        this.regDst = regDst;
    }

    public int getRegSrc1() {
        return regSrc1;
    }

    public int getRegSrc2() {
        return regSrc2;
    }

    public int getRegDst() {
        return regDst;
    }

    @Override
    public String instrToString() {
        return String.format("addu $%s, $%s, $%s",
                RegisterFile.getRegisterName(regDst),
                RegisterFile.getRegisterName(regSrc1),
                RegisterFile.getRegisterName(regSrc2));
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        int src1 = rf.read(regSrc1);
        int src2 = rf.read(regSrc2);
        int result = src1 + src2;
        rf.write(regDst, result);
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
