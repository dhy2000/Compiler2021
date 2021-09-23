package com.compiler.model.token;

public abstract class Token {

    private final TokenType type;
    private final int lineNumber;

    public Token(TokenType type, int lineNumber) {
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public boolean isReserved() {
        return type.isReserved();
    }

    public String getContent() {
        return type.getContent();
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
