package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;

public class Syscall extends MipsInstruction {

    public static final int PRINT_INTEGER = 1;
    public static final int PRINT_STRING = 4;
    public static final int READ_INTEGER = 5;
    public static final int TERMINATE = 10;

    @Override
    public String instrToString() {
        return "syscall";
    }
}
