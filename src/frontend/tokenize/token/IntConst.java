package frontend.tokenize.token;

public class IntConst extends Token {

    /**
     * 32 位有符号整数
     */
    private final int value;

    public IntConst(String literal, int line) {
        super(Type.INTCON, line, literal);
        this.value = Integer.parseInt(literal);
    }

    public int getValue() {
        return value;
    }
}
