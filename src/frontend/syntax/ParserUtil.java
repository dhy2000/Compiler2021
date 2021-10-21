package frontend.syntax;

import exception.UnexpectedEofException;
import exception.UnexpectedTokenException;
import frontend.lexical.token.Token;

import java.util.Iterator;
import java.util.ListIterator;

public class ParserUtil {
    public static Token getSpecifiedToken(Token.Type type, String syntaxName,
                                          Iterator<Token> iterator, int maxLineNum)
            throws UnexpectedTokenException, UnexpectedEofException {
        // Missing right parenthesis or bracket processing
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntaxName);
        }
        Token next = iterator.next();
        if (!next.getType().equals(type)) {
            throw new UnexpectedTokenException(next.lineNumber(), syntaxName, next, type);
        }
        return next;
    }

    public static Token getNullableToken(Token.Type type, String syntaxName,
                                         ListIterator<Token> iterator, int maxLineNum)
            throws UnexpectedEofException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntaxName);
        }
        Token next = iterator.next();
        if (!next.getType().equals(type)) {
            iterator.previous();
            return null;
        }
        return next;
    }

    public static void detectEof(String syntaxName, Iterator<Token> iterator, int maxLineNum) throws UnexpectedEofException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntaxName);
        }
    }
}
