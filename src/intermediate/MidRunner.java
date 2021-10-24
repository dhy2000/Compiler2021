package intermediate;

import config.Config;
import intermediate.code.*;
import intermediate.operand.Immediate;
import intermediate.operand.Operand;
import intermediate.symbol.FuncMeta;
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

    private static final int INSTRUCTION_LIMIT = 100000000;
    private int instrCount = 0;

    // Program Counter
    private ILinkNode currentProgram;

    // Memory Model
    private static final int MEMORY_TOP = 50000000;
    private final ArrayList<Integer> memory;
    private int stackPointer = MEMORY_TOP;

    // Function Calls
    private int currentStackSize; // 当前层已经用掉的栈的大小
    private int retValue; // 返回值, 对应 $v0 寄存器
    private final Stack<Call> retProgram = new Stack<>(); // 返回地址, 对应 $ra 寄存器，指向 Call 指令
    private final Stack<Integer> stackSizeStack = new Stack<>(); // 维护每一层用的栈的大小, 遇 Call 压栈，遇 RETURN 弹栈

    // Temporary Variable
    // Attention: Should protect temporary variables when recursion occurred!
    private Map<String, Integer> tempVariables;
    private final Stack<Map<String, Integer>> tempVariableStack = new Stack<>();

    private int loadMemoryWord(int address) {
        return memory.get(address / 4);
    }

    private void storeMemoryWord(int address, int value) {
        if (ENABLE_DEBUG) {
            debug.printf("%8d@: *%08x <= %d\n", instrCount, address, value);
        }
        memory.set(address / 4, value);
    }

    public MidRunner(Intermediate ir) {
        this.intermediate = ir;
        currentProgram = ir.getMainFunction().getBody().getHead();
        currentStackSize = 0;
        tempVariables = new HashMap<>();
        final int wordCount = MEMORY_TOP / 4;
        memory = new ArrayList<>(wordCount);
        Random random = new Random();
        for (int i = 0; i < wordCount; i++) {
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

    private int getSymbolAddress(Symbol sym) {
        assert sym.hasAddress();
        if (sym.isLocal()) {
            currentStackSize = Math.max(currentStackSize, sym.getAddress());
            return stackPointer - sym.getAddress();
        } else {
            return sym.getAddress();
        }
    }

    private int readOperand(Operand src) {
        if (src instanceof Immediate) {
            return ((Immediate) src).getValue();
        } else if (src instanceof Symbol) {
            if (((Symbol) src).hasAddress()) {
                if (((Symbol) src).isLocal()) {
                    currentStackSize = Math.max(currentStackSize, ((Symbol) src).getAddress());
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
        if (ENABLE_DEBUG) {
            debug.printf("%8d@: $[%s] <= %d\n", instrCount, symbol, value);
        }
        if (symbol.hasAddress()) {
            int address;
            if (symbol.isLocal()) {
                currentStackSize = Math.max(currentStackSize, symbol.getAddress());
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

    private void runUnaryOp(UnaryOp code) {
        UnaryOp.Op op = code.getOp();
        int src = readOperand(code.getSrc());
        int result;
        switch (op) {
            case MOV: result = src; break;
            case NEG: result = -src; break;
            case NOT: result = (src != 0) ? 0 : 1; break;
            default: throw new AssertionError("Bad UnaryOp");
        }
        writeToSymbol(code.getDst(), result);
    }

    private void runIO(ILinkNode code) {
        assert code instanceof PrintFormat || code instanceof Input || code instanceof PrintInt || code instanceof PrintStr;
        if (code instanceof Input) {
            Symbol symbol = ((Input) code).getDst();
            int value = input.nextInt();
            if (symbol.getType().equals(Symbol.Type.INT)) {
                writeToSymbol(symbol, value);
            } else {
                assert symbol.getType().equals(Symbol.Type.POINTER);
                int address = readOperand(symbol);
                storeMemoryWord(address, value);
            }
        } else if (code instanceof PrintInt) {
            int value = readOperand(((PrintInt) code).getValue());
            output.print(value);
        } else if (code instanceof PrintStr) {
            String content = intermediate.getGlobalStrings().get(((PrintStr) code).getLabel());
            output.print(content.replaceAll("\\\\n", "\n"));
        } else {
            throw new AssertionError("Bad IO Code");
        }
    }

    private void runCall(Call code) {
        // Step 1: store stackSize, return Address, Temporary Variables
        stackSizeStack.push(currentStackSize);
        retProgram.push(code);
        tempVariableStack.push(tempVariables);
        // Step 3: load params
        int len = code.getParams().size();
        FuncMeta meta = code.getFunction();
        assert len == meta.getParams().size();
        for (int i = 0; i < len; i++) {
            int value = readOperand(code.getParams().get(i));
            Symbol arg = meta.getParams().get(i);
            int address = (stackPointer - currentStackSize) - arg.getAddress();
            storeMemoryWord(address, value);
        }
        // Step 4: move stack pointer and jump program counter
        tempVariables = new HashMap<>(); // sub-process temp variables
        stackPointer -= currentStackSize;
        currentStackSize = meta.getParamTable().capacity();
        currentProgram = meta.getBody().getHead();
    }

    private void runReturn(Return code) {
        // Step 1: set Return Value if it has
        if (code.hasValue()) {
            retValue = readOperand(code.getValue());
        }
        if (stackSizeStack.isEmpty() || retProgram.isEmpty()) {
            // Terminate! Main Function Return!
            currentProgram = null;
            return;
        }
        // Step 2: restore stack
        currentStackSize = stackSizeStack.pop();
        stackPointer += currentStackSize;
        // Step 3: restore context
        Call call = retProgram.pop();
        tempVariables = tempVariableStack.pop();
        // Step 4: return value
        if (call.hasRet()) {
            writeToSymbol(call.getRet(), retValue);
        }
        currentProgram = call.getNext();
    }

    private void runAddressOffset(AddressOffset code) {
        Symbol base = code.getBase();
        int offset = readOperand(code.getOffset());
        Symbol target = code.getTarget();
        assert target.getType().equals(Symbol.Type.POINTER);
        assert (base.getType().equals(Symbol.Type.ARRAY) && base.hasAddress()) || (base.getType().equals(Symbol.Type.POINTER));
        if (base.getType().equals(Symbol.Type.ARRAY)) {
            assert base.hasAddress();
            int address = getSymbolAddress(base);
            writeToSymbol(target, address + offset);
        } else {
            int address = readOperand(base);
            writeToSymbol(target, address + offset);
        }
    }

    private void runPointerOp(PointerOp code) {
        PointerOp.Op op = code.getOp();
        int address = readOperand(code.getAddress());
        int value;
        switch (op) {
            case LOAD:
                value = loadMemoryWord(address);
                Symbol dst = code.getDst();
                writeToSymbol(dst, value);
                break;
            case STORE:
                value = readOperand(code.getSrc());
                storeMemoryWord(address, value);
                break;
            default:
                throw new AssertionError("Bad PointerOp");
        }
    }

    private void runBranchOrJump(ILinkNode code) {
        assert code instanceof Jump || code instanceof BranchIfElse;
        if (code instanceof Jump) {
            currentProgram = ((Jump) code).getTarget().getHead();
        } else {
            int cond = readOperand(((BranchIfElse) code).getCondition());
            if (cond != 0) {
                currentProgram = ((BranchIfElse) code).getThenTarget().getHead();
            } else {
                currentProgram = ((BranchIfElse) code).getElseTarget().getHead();
            }
        }
    }

    /**
     * 执行一步
     */
    private void step() {
        assert isRunning();
        instrCount++;

        if (ENABLE_DEBUG) {
            debug.printf("%8d[sp=0x%08x]: %s\n", instrCount, stackPointer, currentProgram);
        }

        if (currentProgram instanceof BinaryOp) {
            runBinaryOp((BinaryOp) currentProgram);
            currentProgram = currentProgram.getNext();
        } else if (currentProgram instanceof UnaryOp) {
            runUnaryOp((UnaryOp) currentProgram);
            currentProgram = currentProgram.getNext();
        } else if (currentProgram instanceof Input || currentProgram instanceof PrintInt || currentProgram instanceof PrintStr) {
            runIO(currentProgram);
            currentProgram = currentProgram.getNext();
        } else if (currentProgram instanceof Call) {
            runCall((Call) currentProgram);
        } else if (currentProgram instanceof Return) {
            runReturn((Return) currentProgram);
        } else if (currentProgram instanceof AddressOffset) {
            runAddressOffset((AddressOffset) currentProgram);
            currentProgram = currentProgram.getNext();
        } else if (currentProgram instanceof PointerOp) {
            runPointerOp((PointerOp) currentProgram);
            currentProgram = currentProgram.getNext();
        } else if (currentProgram instanceof Jump || currentProgram instanceof BranchIfElse) {
            runBranchOrJump(currentProgram);
        } else {
            throw new AssertionError("Bad Mid Code!");
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
