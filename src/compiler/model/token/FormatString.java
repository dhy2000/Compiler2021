package compiler.model.token;

public class FormatString extends Token {

    /**
     * 不包括双引号
     */
    private final String content;

    public FormatString(String content) {
        super(TokenType.STRCON);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "\"" + getContent() + "\"";
    }
}
