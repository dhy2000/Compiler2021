package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

/**
 * jr 指令，跳转到寄存器, 在写模拟器时需要特殊处理
 */
public class JumpRegister extends MipsInstruction {

    private final int regSrc;

    public JumpRegister(int regSrc) {
        this.regSrc = regSrc;
    }

    public int getRegSrc() {
        return regSrc;
    }

    @Override
    public String instrToString() {
        return "jr $" + RegisterFile.getRegisterName(regSrc);
    }
}
