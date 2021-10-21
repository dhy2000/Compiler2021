package exception;

import frontend.lexical.token.Token;

import java.util.Objects;

public class UnexpectedTokenException extends FrontendException {

    private final String syntax;
    private final Token.Type expected;
    private final Token got;

    public UnexpectedTokenException(int line, String syntaxName, Token got, Token.Type expected) {
        super("Unexpected Token", line, syntaxName + " got " + got.getContent() + " but expected " + expected.name());
        this.syntax = syntaxName;
        this.got = got;
        this.expected = expected;
    }

    public UnexpectedTokenException(int line, String syntaxName, Token got) {
        super("Unexpected Token", line, syntaxName + " got unexpected token " + got.getContent());
        this.syntax = syntaxName;
        this.got = got;
        this.expected = null;
    }

    public String getSyntax() {
        return syntax;
    }

    public Token getGot() {
        return got;
    }

    public Token.Type getExpected() {
        return expected;
    }

    public boolean hasExpected() {
        return Objects.nonNull(expected);
    }
}
