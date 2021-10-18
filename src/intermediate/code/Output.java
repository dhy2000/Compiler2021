package intermediate.code;

import intermediate.operand.Operand;

import java.util.Collections;
import java.util.List;

public class Output extends ILinkNode {
    private final String format;    // 格式字符串对应全局的标签
    private final List<Operand> params;

    public Output(String format, List<Operand> params) {
        this.format = format;
        this.params = Collections.unmodifiableList(params);
    }

    public String getFormat() {
        return format;
    }

    public List<Operand> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "PRINTF " + format + ", [" + params.stream().map(Object::toString).reduce((s, s2) -> s + ", " + s2).orElse("") + "]";
    }
}
