package middle.operand;

/**
 * 中间代码中的立即数
 */
public class Immediate implements Operand {
    private final int value;

    public Immediate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
