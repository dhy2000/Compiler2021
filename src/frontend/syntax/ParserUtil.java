package frontend.syntax;

import exception.EofException;
import exception.WrongTokenException;
import frontend.lexical.token.Token;

import java.util.Iterator;
import java.util.ListIterator;

public class ParserUtil {
    public static Token getSpecifiedToken(Token.Type type, String syntaxName,
                                          Iterator<Token> iterator, int maxLineNum)
            throws WrongTokenException, EofException {
        // Missing right parenthesis or bracket processing
        if (!iterator.hasNext()) {
            throw new EofException(maxLineNum, syntaxName);
        }
        Token next = iterator.next();
        if (!next.getType().equals(type)) {
            throw new WrongTokenException(next.lineNumber(), syntaxName, next, type);
        }
        return next;
    }

    public static Token getNullableToken(Token.Type type, String syntaxName,
                                         ListIterator<Token> iterator, int maxLineNum)
            throws EofException {
        if (!iterator.hasNext()) {
            throw new EofException(maxLineNum, syntaxName);
        }
        Token next = iterator.next();
        if (!next.getType().equals(type)) {
            iterator.previous();
            return null;
        }
        return next;
    }

    public static void detectEof(String syntaxName, Iterator<Token> iterator, int maxLineNum) throws EofException {
        if (!iterator.hasNext()) {
            throw new EofException(maxLineNum, syntaxName);
        }
    }
}
