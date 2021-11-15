package backend;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;
import backend.instruction.JumpRegister;
import backend.instruction.MipsInstruction;
import backend.instruction.Syscall;
import config.Config;
import utility.ReaderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;

/**
 * MIPS 模拟器, 模拟执行 MIPS 代码
 */
public class Simulator {
    private static final boolean ENABLE_TRACE = false;

    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private final PrintStream output = Config.getTarget();
    private final PrintStream debug = System.err;

    public static final int PC_START = 0x00400000;

    private final Map<Integer, MipsInstruction> instructionMemory = new TreeMap<>(); // PC --> Instruction
    // 标签对应的指令 PC (变量不体现标签名, 目标代码中所有的标签仅为跳转服务)
    private final Map<String, Integer> labelToAddress = new HashMap<>();

    public static final int INSTRUCTION_LIMIT = 10000000;
    private int instrCount = 0;

    private final RegisterFile rf = new RegisterFile();
    private final Memory memory = new Memory();

    private StringBuilder outputBuffer;

    public Simulator(Mips mips) {
        // 加载指令
        int pc = PC_START;
        MipsInstruction instr = mips.getFirstInstruction();
        while (instr.hasNext()) {
            instructionMemory.put(pc, instr);
            if (instr.hasLabel()) {
                labelToAddress.put(instr.getLabel(), pc);
            }
            debug.printf("0x%08x: %s\n", pc, instr.instrToString());
            pc += 4;
            instr = (MipsInstruction) instr.getNext();
        }

        // 加载内存
        List<Integer> modifiedMem = mips.getInitMem().modifiedAddresses();
        List<Integer> storedStrings = mips.getInitMem().storedStringAddresses();
        modifiedMem.forEach(integer -> memory.storeWord(integer, mips.getInitMem().loadWord(integer)));
        storedStrings.forEach(integer -> memory.putString(integer, mips.getInitMem().getString(integer)));
    }

    public void runAll(boolean stopAtInput) {
        boolean running = true;
        outputBuffer = new StringBuilder();
        while (running && instrCount <= INSTRUCTION_LIMIT) {
            MipsInstruction instr = instructionMemory.get(rf.getProgramCounter());
            instrCount++;
            if (ENABLE_TRACE) {
                debug.printf("At pc=0x%x: %s\n", rf.getProgramCounter(), instr.instrToString());
            }
            int readInt;
            if (instr instanceof Syscall) {
                switch (rf.read(RegisterFile.Register.V0)) {
                    case Syscall.PRINT_INTEGER:
                        output.print(rf.read(RegisterFile.Register.A0));
                        break;
                    case Syscall.PRINT_STRING:
                        output.print(memory.getString(rf.read(RegisterFile.Register.A0)).replace("\\n", "\n"));
                        break;
                    case Syscall.READ_INTEGER:
                        if (!stopAtInput) {
                            readInt = ReaderUtil.readInt(input);
                            rf.write(RegisterFile.Register.V0, readInt);
                        } else {
                            running = false;
                        }
                        break;
                    case Syscall.TERMINATE:
                        running = false;
                        break;
                    default: throw new AssertionError("Undefined Syscall Number!");
                }
                rf.addProgramCounter(4);
            } else {
                instr.execute(rf, memory);
                if (instr.isJump(rf)) {
                    if (!(instr instanceof JumpRegister)) {
                        String label = instr.getJumpTarget();
                        if (labelToAddress.containsKey(label)) {
                            rf.setProgramCounter(labelToAddress.get(label));
                        } else {
                            throw new AssertionError("Label not exist!");
                        }
                    }
                } else {
                    rf.addProgramCounter(4);
                }
            }
        }
        try {
            input.close();
        } catch (IOException e) {
            throw new AssertionError("Error closing input.");
        }
    }

    public String getOutputContent() {
        if (Objects.isNull(outputBuffer)) {
            return "";
        }
        return outputBuffer.toString();
    }

}
