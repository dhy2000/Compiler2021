package intermediate.symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * 函数定义表
 */
public class FuncTable {
    private final Map<String, FuncDef> funcDefs = new HashMap<>();

    public FuncTable() {}

    public void addFunc(FuncDef func) {
        funcDefs.putIfAbsent(func.getName(), func);
    }

    public FuncDef get(String name) {
        return funcDefs.get(name);
    }

    public boolean contains(String name) {
        return funcDefs.containsKey(name);
    }
}
