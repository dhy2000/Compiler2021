package com.compiler.model.token;

public class Ident extends Token {

    private final String identifier;

    public Ident(int lineNumber, String identifier) {
        super(TokenType.IDENFR, lineNumber);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getContent() {
        return identifier;
    }
}
