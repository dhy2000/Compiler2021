package frontend.lexical.token;

public class FormatString extends Token {

    /**
     * 不包括双引号
     */
    private final String inner;

    public FormatString(String str, int line) {
        super(Type.STRCON, line, str);
        assert str.length() >= 2 && str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"';
        this.inner = str.substring(1, str.length() - 1);
        // TODO: check invalid character
    }

    public String getInner() {
        return inner;
    }
}
