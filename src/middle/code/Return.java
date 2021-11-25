package middle.code;

import middle.operand.Operand;

import java.util.Objects;

/**
 * 从函数中返回
 */
public class Return extends ILinkNode {
    private final Operand value; // Nullable

    public Return() {
        this.value = null;
    }

    public Return(Operand value) {
        this.value = value;
    }

    public Operand getValue() {
        return value;
    }

    public boolean hasValue() {
        return Objects.nonNull(value);
    }

    @Override
    public String toString() {
        return "RETURN" + (hasValue() ? " " + value : "");
    }
}
