package intermediate;

import config.Config;
import intermediate.code.BinaryOp;
import intermediate.code.ILinkNode;
import intermediate.operand.Immediate;
import intermediate.operand.Operand;
import intermediate.symbol.Symbol;

import java.io.PrintStream;
import java.util.*;

/**
 * 执行中间代码（虚拟机）
 */
public class MidRunner {
    private static final boolean ENABLE_DEBUG = true;

    private final Intermediate intermediate;
    private final Scanner input = new Scanner(System.in);
    private final PrintStream output = Config.getTarget();
    private final PrintStream debug = System.err;

    private static final int INSTRUCTION_LIMIT = 1000000;
    private int instrCount = 0;

    // Program Counter
    private ILinkNode currentProgram;

    // Memory Model
    private static final int STACK_TOP = 500000;
    private final ArrayList<Integer> memory;
    private int stackPointer = STACK_TOP;

    // Temporary Variable
    private final Map<String, Integer> tempVariables = new HashMap<>();

    private int loadMemoryWord(int address) {
        return memory.get(address / 4);
    }

    private void storeMemoryWord(int address, int value) {
        memory.set(address / 4, value);
    }

    public MidRunner(Intermediate ir) {
        this.intermediate = ir;
        currentProgram = ir.getMainFunction().getBody().getHead();
        memory = new ArrayList<>(STACK_TOP);
        Random random = new Random();
        for (int i = 0; i < STACK_TOP; i++) {
            memory.add(random.nextInt());
        }
        for (Map.Entry<String, Integer> entry : ir.getGlobalVariables().entrySet()) {
            String name = entry.getKey();
            int value = entry.getValue();
            int address = ir.getGlobalAddress().get(name);
            storeMemoryWord(address, value);
        }
        for (Map.Entry<String, List<Integer>> entry : ir.getGlobalArrays().entrySet()) {
            String name = entry.getKey();
            List<Integer> values = entry.getValue();
            int address = ir.getGlobalAddress().get(name);
            for (int value : values) {
                storeMemoryWord(address, value);
                address += Symbol.SIZEOF_INT;
            }
        }
    }

    /**
     * 判断中间代码执行是否终止
     * @return 是否终止
     */
    private boolean isRunning() {
        return Objects.nonNull(currentProgram);
    }

    private int readOperand(Operand src) {
        if (src instanceof Immediate) {
            return ((Immediate) src).getValue();
        } else if (src instanceof Symbol) {
            if (((Symbol) src).hasAddress()) {
                if (((Symbol) src).isLocal()) {
                    return loadMemoryWord(stackPointer - ((Symbol) src).getAddress());
                } else {
                    return loadMemoryWord(((Symbol) src).getAddress());
                }
            } else {
                return tempVariables.get(((Symbol) src).getName());
            }
        } else {
            throw new AssertionError("Bad type of Operand");
        }
    }

    private void writeToSymbol(Symbol symbol, int value) {
        if (symbol.hasAddress()) {
            int address;
            if (symbol.isLocal()) {
                address = stackPointer - symbol.getAddress();
            } else {
                address = symbol.getAddress();
            }
            storeMemoryWord(address, value);
        } else {
            tempVariables.put(symbol.getName(), value);
        }
    }

    private void runBinaryOp(BinaryOp code) {
        BinaryOp.Op op = code.getOp();
        int src1 = readOperand(code.getSrc1());
        int src2 = readOperand(code.getSrc2());
        int result;
        switch (op) {
            case ADD: result = src1 + src2; break;
            case SUB: result = src1 - src2; break;
            case AND: result = ((src1 != 0) && (src2 != 0)) ? 1 : 0; break;
            case OR: result = ((src1 != 0) || (src2 != 0)) ? 1 : 0; break;
            case MUL: result = src1 * src2; break;
            case DIV: result = src1 / src2; break;
            case MOD: result = src1 % src2; break;
            case GE: result = (src1 >= src2) ? 1 : 0; break;
            case GT: result = (src1 > src2) ? 1 : 0; break;
            case LE: result = (src1 <= src2) ? 1 : 0; break;
            case LT: result = (src1 < src2) ? 1 : 0; break;
            case EQ: result = (src1 == src2) ? 1 : 0; break;
            case NE: result = (src1 != src2) ? 1 : 0; break;
            default: throw new AssertionError("Bad BinaryOp");
        }
        writeToSymbol(code.getDst(), result);
    }

    /**
     * 执行一步
     */
    private void step() {
        assert isRunning();
        instrCount++;

        if (ENABLE_DEBUG) {
            debug.printf("%d: %s\n", instrCount, currentProgram);
        }



    }

    /**
     * 自动执行
     */
    public void run() {
        while (isRunning() && instrCount <= INSTRUCTION_LIMIT) {
            step();
        }
        if (isRunning()) {
            if (ENABLE_DEBUG) {
                debug.println("Count of instructions out of limit, maybe TLE happen.");
            }
        }
    }

}
