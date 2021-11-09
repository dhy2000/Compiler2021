package backend.hardware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterFile {
    private static final boolean ENABLE_TRACE = true;

    private static final int REGISTER_NUMBER = 32;

    private static final int TEXT_START = 0x00400000;

    private static final int GLOBAL_POINTER_INIT = 0x10008000;
    private static final int STACK_POINTER_INIT = 0x7fffeffc;

    private final List<Integer> registers = new ArrayList<>(REGISTER_NUMBER);

    private final AtomicInteger programCounter = new AtomicInteger(TEXT_START);
    private final AtomicInteger hi = new AtomicInteger(0);
    private final AtomicInteger lo = new AtomicInteger(0);

    private static final List<String> names = Collections.unmodifiableList(Arrays.asList(
            "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
            "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra"));

    public static class Register {
        public static final int ZERO = 0;
        public static final int AT = 1;
        public static final int V0 = 2;
        public static final int V1 = 3;
        public static final int A0 = 4;
        public static final int A1 = 5;
        public static final int A2 = 6;
        public static final int A3 = 7;
        public static final int T0 = 8;
        public static final int T1 = 9;
        public static final int T2 = 10;
        public static final int T3 = 11;
        public static final int T4 = 12;
        public static final int T5 = 13;
        public static final int T6 = 14;
        public static final int T7 = 15;
        public static final int S0 = 16;
        public static final int S1 = 17;
        public static final int S2 = 18;
        public static final int S3 = 19;
        public static final int S4 = 20;
        public static final int S5 = 21;
        public static final int S6 = 22;
        public static final int S7 = 23;
        public static final int T8 = 24;
        public static final int T9 = 25;
        public static final int K0 = 26;
        public static final int K1 = 27;
        public static final int GP = 28;
        public static final int SP = 29;
        public static final int FP = 30;
        public static final int RA = 31;
    }

    public RegisterFile() {
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers.add(0);
        }
        registers.set(Register.GP, GLOBAL_POINTER_INIT);
        registers.set(Register.SP, STACK_POINTER_INIT);
    }

    public static String getRegisterName(int id) {
        assert id >= 0 && id < REGISTER_NUMBER;
        return names.get(id);
    }

    public int read(int id) {
        assert id >= 0 && id < REGISTER_NUMBER;
        return registers.get(id);
    }

    public void write(int id, int value) {
        assert id >= 0 && id < REGISTER_NUMBER;
        if (id == 0) {
            return;
        }
        if (ENABLE_TRACE) {
            System.err.printf("$%2d <= %08x", id, value);
        }
        registers.set(id, value);
    }

    public int getProgramCounter() {
        return programCounter.get();
    }

    public int addProgramCounter(int value) {
        return programCounter.addAndGet(value);
    }

    public void setProgramCounter(int value) {
        programCounter.set(value);
    }

    public int getHi() {
        return hi.get();
    }

    public int getLo() {
        return lo.get();
    }

    public void setHi(int value) {
        hi.set(value);
    }

    public void setLo(int value) {
        lo.set(value);
    }
}
