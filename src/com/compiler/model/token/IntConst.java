package com.compiler.model.token;

public class IntConst extends Token {

    /**
     * 32 位有符号整数
     */
    private final int value;

    public IntConst(int value) {
        super(TokenType.INTCON);
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
