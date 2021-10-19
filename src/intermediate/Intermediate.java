package intermediate;

import intermediate.code.FunctionBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中间代码的总体类，包括各个函数的入口以及全局变量
 */
public class Intermediate {
    private final Map<String, Integer> globalVariables;
    private final Map<String, List<Integer>> globalArrays;  // 展平的数组初值
    private final Map<String, String> globalStrings;        // FormatString
    private final Map<String, FunctionBlock> functions;

    private FunctionBlock mainFunction;

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
    public void addGlobalString(String s) {
        stringCount = stringCount + 1;
        globalStrings.put("STR_" + stringCount, s);
    }

    public Map<String, FunctionBlock> getFunctions() {
        return functions;
    }

    public void putFunction(FunctionBlock function) {
        functions.put(function.getLabel(), function);
    }

    public void setMainFunction(FunctionBlock main) {
        mainFunction = main;
    }

    public FunctionBlock getMainFunction() {
        return mainFunction;
    }
}
