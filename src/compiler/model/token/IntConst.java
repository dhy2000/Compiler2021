package compiler.model.token;

public class IntConst extends Token {

    /**
     * 32 位有符号整数
     */
    private final String raw;
    private final int value;

    public IntConst(String raw) {
        super(TokenType.INTCON);
        this.raw = raw;
        this.value = Integer.parseInt(raw);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getContent() {
        return raw;
    }
}
