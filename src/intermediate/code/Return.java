package intermediate.code;

import intermediate.operand.Operand;

import java.util.Objects;

/**
 * 函数返回的中间代码, 返回语句不需要接下句
 */
public class Return extends IntermediateCode {

    private final Operand value; // Nullable

    public Return(
            Operand value,
            String function, String block, int index) {
        super(function, block, index);
        this.value = value;
    }

    public boolean hasValue() {
        return Objects.nonNull(value);
    }

    public Operand getValue() {
        return value;
    }

    @Override
    public String getName() {
        return "RETURN";
    }

    @Override
    public String toString() {
        return getName() + (hasValue() ? " " + getValue() : "");
    }
}
