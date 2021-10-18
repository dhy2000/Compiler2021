package intermediate.code;

import intermediate.operand.Symbol;

import java.util.Collections;
import java.util.List;

public class FunctionBlock extends BasicBlock {

    private final List<Symbol> params; // 函数参数的符号表

    public FunctionBlock(String name, List<Symbol> params) {
        super(name);
        this.params = Collections.unmodifiableList(params);
    }

    public List<Symbol> getParams() {
        return params;
    }
}
