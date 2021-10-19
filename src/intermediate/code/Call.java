package intermediate.code;

import intermediate.operand.Operand;
import intermediate.symbol.FuncMeta;
import intermediate.symbol.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 函数调用
 */
public class Call extends ILinkNode {
    private final FuncMeta function;
    private final List<Operand> params;
    private final Symbol ret;

    public Call(FuncMeta function, List<Operand> params) {
        this.function = function;
        this.params = Collections.unmodifiableList(params);
        this.ret = null;
    }

    public Call(FuncMeta function, List<Operand> params, Symbol ret) {
        this.function = function;
        this.params = Collections.unmodifiableList(params);
        this.ret = ret;
    }

    public FuncMeta getFunction() {
        return function;
    }

    public List<Operand> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "CALL " + function.getName() + ", ["
                + params.stream().map(Object::toString).reduce((s, s2) -> s + ", " + s2).orElse("") + "]"
                + (Objects.nonNull(ret) ? " -> " + ret : "");
    }
}
