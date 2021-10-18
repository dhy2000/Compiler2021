package intermediate.code;

import intermediate.operand.Operand;

import java.util.Collections;
import java.util.List;

/**
 * 函数调用
 */
public class Call extends ILinkNode {
    private final FunctionBlock function;
    private final List<Operand> params;

    public Call(FunctionBlock function, List<Operand> params) {
        this.function = function;
        this.params = Collections.unmodifiableList(params);
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
