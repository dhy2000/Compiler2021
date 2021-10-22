package intermediate;

import intermediate.code.BasicBlock;
import intermediate.code.BranchIfElse;
import intermediate.code.ILinkNode;
import intermediate.code.Jump;
import intermediate.symbol.FuncMeta;

import java.io.PrintStream;
import java.util.*;

/**
 * 中间代码的总体类，包括各个函数的入口以及全局变量
 */
public class Intermediate {
    private final Map<String, Integer> globalVariables;
    private final Map<String, List<Integer>> globalArrays;  // 展平的数组初值
    private final Map<String, String> globalStrings;        // FormatString
    private final Map<String, FuncMeta> functions;

    private FuncMeta mainFunction;

    public Intermediate() {
        this.globalVariables = new HashMap<>();
        this.globalArrays = new HashMap<>();
        this.globalStrings = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public Map<String, Integer> getGlobalVariables() {
        return globalVariables;
    }

    public void addGlobalVariable(String name, int value) {
        globalVariables.put(name, value);
    }

    public Map<String, List<Integer>> getGlobalArrays() {
        return globalArrays;
    }

    public void addGlobalArray(String name, List<Integer> values) {
        globalArrays.put(name, values);
    }

    public Map<String, String> getGlobalStrings() {
        return globalStrings;
    }

    private int stringCount = 0;
    public String addGlobalString(String s) {
        stringCount = stringCount + 1;
        String label = "STR_" + stringCount;
        globalStrings.put("STR_" + stringCount, s);
        return label;
    }

    public Map<String, FuncMeta> getFunctions() {
        return functions;
    }

    public void putFunction(FuncMeta function) {
        functions.put(function.getName(), function);
    }

    public void setMainFunction(FuncMeta main) {
        mainFunction = main;
    }

    public FuncMeta getMainFunction() {
        return mainFunction;
    }

    public void output(PrintStream ps) {
        // global variables
        ps.println(" ====== IR ====== ");
        ps.println(" = Global Variables = ");
        for (Map.Entry<String, Integer> entry : globalVariables.entrySet()) {
            ps.printf("%s: %d\n", entry.getKey(), entry.getValue());
        }
        ps.println((" = Global Arrays = "));
        for (Map.Entry<String, List<Integer>> entry : globalArrays.entrySet()) {
            ps.printf("%s: [%s]\n", entry.getKey(), entry.getValue().stream().map(Object::toString).reduce((s, s2) -> s + ", " + s2).orElse(""));
        }
        ps.println(" = Global Strings = ");
        for (Map.Entry<String, String> entry : globalStrings.entrySet()) {
            ps.printf("%s: \"%s\"\n", entry.getKey(), entry.getValue());
        }
        ps.println(" = Text = ");
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>(); // BFS

        for (FuncMeta func : functions.values()) {
            queue.offer(func.getBody());
            while (!queue.isEmpty()) {
                BasicBlock front = queue.poll();
                if (visited.contains(front)) {
                    continue;
                }
                visited.add(front);
                ps.println(front.getLabel() + ":");
                ILinkNode node = front.getHead();
                while (Objects.nonNull(node) && node.hasNext()) {
                    if (node instanceof Jump) {
                        BasicBlock target = ((Jump) node).getTarget();
                        queue.offer(target);
                    } else if (node instanceof BranchIfElse) {
                        BasicBlock then = ((BranchIfElse) node).getThenTarget();
                        queue.offer(then);
                        BasicBlock elseBlk = ((BranchIfElse) node).getElseTarget();
                        queue.offer(elseBlk);
                    }
                    ps.println("    " + node);
                    node = node.getNext();
                }
                ps.println();
            }
        }
    }
}
