package intermediate.code;

import intermediate.operand.Operand;

/**
 * 输出整数，对应 Mars 的 1 号 syscall
 */
public class PrintInt extends ILinkNode {
    private final Operand value;

    public PrintInt(Operand value) {
        this.value = value;
    }

    public Operand getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PRINT_INT " + value;
    }
}
