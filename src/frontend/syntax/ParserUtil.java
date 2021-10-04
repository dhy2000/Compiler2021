package frontend.syntax;

import frontend.error.exception.syntax.UnexpectedEofException;
import frontend.error.exception.syntax.UnexpectedTokenException;
import frontend.lexical.token.Token;

import java.util.Iterator;

public class ParserUtil {
    public static Token getSpecifiedToken(Token.Type type, String syntaxName, Iterator<Token> iterator, int maxLineNum) throws UnexpectedTokenException, UnexpectedEofException {
        // TODO: Missing right parenthesis or bracket processing
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntaxName);
        }
        Token next = iterator.next();
        if (!next.getType().equals(type)) {
            throw new UnexpectedTokenException(next.lineNumber(), syntaxName, next, type);
        }
        return next;
    }

    public static void detectEof(String syntaxName, Iterator<Token> iterator, int maxLineNum) throws UnexpectedEofException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntaxName);
        }
    }
}
