package frontend.lexical;

import frontend.lexical.token.Token;

import java.io.PrintStream;
import java.util.*;

public class TokenList {
    private final List<Token> tokens = new LinkedList<>();
    private int maxLineNumber = 0;

    public TokenList() {}

    public void append(Token token) {
        tokens.add(token);
        maxLineNumber = Math.max(maxLineNumber, token.lineNumber());
    }

    public int getMaxLineNumber() {
        return maxLineNumber;
    }

    public void output(PrintStream out) {
        tokens.forEach(token -> token.output(out));
    }

    public ListIterator<Token> listIterator() {
        return tokens.listIterator();
    }
}
