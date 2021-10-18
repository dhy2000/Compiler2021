package intermediate;

import intermediate.code.FunctionBlock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 中间代码的总体类，包括各个函数的入口以及全局变量
 */
public class Intermediate {
    private final Map<String, Integer> globalVariables;
    private final Map<String, List<Integer>> globalArrays;
    private final Map<String, String> globalStrings;
    private final Map<String, FunctionBlock> functions;

    public Intermediate(Map<String, Integer> globalVariables,
                        Map<String, List<Integer>> globalArrays,
                        Map<String, String> globalStrings,
                        Map<String, FunctionBlock> functions) {
        this.globalArrays = Collections.unmodifiableMap(globalArrays);
        this.globalVariables = Collections.unmodifiableMap(globalVariables);
        this.globalStrings = Collections.unmodifiableMap(globalStrings);
        this.functions = Collections.unmodifiableMap(functions);
    }

    public Map<String, Integer> getGlobalVariables() {
        return globalVariables;
    }

    public Map<String, List<Integer>> getGlobalArrays() {
        return globalArrays;
    }

    public Map<String, String> getGlobalStrings() {
        return globalStrings;
    }

    public Map<String, FunctionBlock> getFunctions() {
        return functions;
    }
}
