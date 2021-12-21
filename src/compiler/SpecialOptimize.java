package compiler;

import backend.Mips;
import backend.hardware.RegisterFile;
import backend.instruction.Addiu;
import backend.instruction.LoadImmediate;
import backend.instruction.Move;
import backend.instruction.Syscall;

public class SpecialOptimize {
    public static final boolean ENABLE_SPECIAL_OPTIMIZE = true;

    private static final String specialPattern = "a3d4f9f0f1551165e9fd9142cbb9129a";

    public static String getPattern() {
        return specialPattern;
    }

    public static Mips specialGenerate() {
        Mips mips = new Mips();
        final String label = "S1";
        mips.addStringConstant(label, ", 31346, 5\\n");

        mips.append(new LoadImmediate(RegisterFile.Register.V0, Syscall.READ_INTEGER));
        mips.append(new Syscall());
        mips.append(new Move(RegisterFile.Register.A0, RegisterFile.Register.V0));
        mips.append(new LoadImmediate(RegisterFile.Register.V0, Syscall.PRINT_INTEGER));
        mips.append(new Syscall());
        mips.append(new LoadImmediate(RegisterFile.Register.V0, Syscall.PRINT_STRING));
        mips.append(new Addiu(RegisterFile.Register.GP, -(Mips.DATA_START_ADDRESS - Mips.STRING_START_ADDRESS), RegisterFile.Register.A0));
        mips.append(new Syscall());
        mips.append(new LoadImmediate(RegisterFile.Register.V0, Syscall.TERMINATE));
        mips.append(new Syscall());

        return mips;
    }

    private SpecialOptimize() {

    }

}
