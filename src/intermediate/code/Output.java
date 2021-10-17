package intermediate.code;

import intermediate.operand.Operand;

import java.util.Collections;
import java.util.List;

/**
 * 输出语句 printf 的中间代码
 */
public class Output extends IntermediateCode implements InOrder {

    private final String format;
    private final List<Operand> params;

    public Output(
            String format, List<Operand> params,
            String function, String block, int index) {
        super(function, block, index);
        this.format = format;
        this.params = Collections.unmodifiableList(params);
    }

    public String getFormat() {
        return format;
    }

    public List<Operand> getParams() {
        return params;
    }

    private IntermediateCode next;

    @Override
    public IntermediateCode getNext() {
        return next;
    }

    public void setNext(IntermediateCode next) {
        this.next = next;
    }

    @Override
    public String getName() {
        return "PRINTF";
    }

    @Override
    public String toString() {
        return getName() + " " + getFormat() + params.stream().map(Object::toString).reduce(" ", (s, s2) -> s + s2) + " ----> " + next.getLabel();
    }
}
