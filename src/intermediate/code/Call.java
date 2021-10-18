package intermediate.code;

import intermediate.operand.Operand;
import intermediate.operand.Symbol;

import java.util.Collections;
import java.util.List;

/**
 * 函数调用
 */
public class Call extends ILinkNode {
    private final FunctionBlock function;
    private final List<Operand> params;
    private final Symbol ret;

    public Call(FunctionBlock function, List<Operand> params) {
        this.function = function;
        this.params = Collections.unmodifiableList(params);
        this.ret = null;
    }

    public Call(FunctionBlock function, List<Operand> params, Symbol ret) {
        this.function = function;
        this.params = Collections.unmodifiableList(params);
        this.ret = null;
    }

    public FunctionBlock getFunction() {
        return function;
    }

    public List<Operand> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "CALL " + function + ", [" + params.stream().map(Object::toString).reduce((s, s2) -> s + ", " + s2).orElse("") + "]";
    }
}
