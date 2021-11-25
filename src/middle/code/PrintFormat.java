package middle.code;

import middle.operand.Operand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 原始形式的 printf, 在优化环节会被转化成 print_int 和 print_str.
 */
public class PrintFormat extends ILinkNode {
    private final String format;    // 格式字符串对应全局的标签
    private final List<Operand> params;

    public PrintFormat(String format, List<Operand> params) {
        this.format = format;
        this.params = Collections.unmodifiableList(new ArrayList<>(params));
    }

    public String getFormat() {
        return format;
    }

    public List<Operand> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "PRINTF \"" + format + "\", [" + params.stream().map(Object::toString).reduce((s, s2) -> s + ", " + s2).orElse("") + "]";
    }
}
