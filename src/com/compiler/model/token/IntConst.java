package com.compiler.model.token;

public class IntConst extends Token {

    private final int value;

    public IntConst(int lineNumber, int value) {
        super(TokenType.INTCON, lineNumber);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getContent() {
        return String.valueOf(value);
    }
}
