package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;
import config.Config;

public class Syscall extends MipsInstruction {
    @Override
    public String instrToString() {
        return "syscall";
    }

    @Override
    public void execute(RegisterFile rf, Memory mem) {
        int v0 = rf.read(RegisterFile.Register.V0);
        if (v0 == 1) {
            int a0 = rf.read(RegisterFile.Register.A0);
            Config.getTarget().printf("%d", a0);
        } else if (v0 == 4) {
            int address = rf.read(RegisterFile.Register.A0);
            String str = mem.getString(address);
            Config.getTarget().print(str);
        }
    }

    @Override
    public boolean isJump(RegisterFile rf) {
        return false;
    }
}
