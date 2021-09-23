package compiler.model.token;

public class FormatString extends Token {

    /**
     * 不包括双引号
     */
    private final String innerStr;

    public FormatString(String str) {
        super(TokenType.STRCON);
        this.innerStr = str;
    }

    @Override
    public String getContent() {
        return "\"" + innerStr + "\"";
    }
}
