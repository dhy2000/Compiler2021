package intermediate;

import config.Config;
import intermediate.code.ILinkNode;
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

        }
    }

    /**
     * 判断中间代码执行是否终止
     * @return 是否终止
     */
    private boolean isTerminated() {
        return Objects.isNull(currentProgram);
    }

    /**
     * 执行一步
     */
    private void step() {
        assert !isTerminated();
        instrCount++;

        if (ENABLE_DEBUG) {
            debug.printf("%d: %s\n", instrCount, currentProgram);
        }



    }

    /**
     * 自动执行
     */
    public void run() {
        while (!isTerminated() && instrCount <= INSTRUCTION_LIMIT) {
            step();
        }
        if (!isTerminated()) {
            if (ENABLE_DEBUG) {
                debug.println("Count of instructions out of limit, maybe TLE happen.");
            }
        }
    }

}
