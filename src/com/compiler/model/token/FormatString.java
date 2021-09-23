package com.compiler.model.token;

public class FormatString extends Token {

    /**
     * 不包括双引号
     */
    private final String content;

    public FormatString(int lineNumber, String content) {
        super(TokenType.STRCON, lineNumber);
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
