package frontend.error.exception.syntax;

import frontend.error.exception.FrontendException;
import frontend.lexical.token.Token;

public class UnexpectedTokenException extends FrontendException {
    public UnexpectedTokenException(int line, String syntaxName, Token got, Token.Type expected) {
        super("Unexpected Token", line, syntaxName + " got " + got.getContent() + " but expected " + expected.name());
    }

    public UnexpectedTokenException(int line, String syntaxName, Token got) {
        super("Unexpected Token", line, syntaxName + " got unexpected token " + got.getContent());
    }
}
