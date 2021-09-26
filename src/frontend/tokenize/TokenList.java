package frontend.tokenize;

import frontend.tokenize.token.Token;

import java.io.PrintStream;
import java.util.*;

public class TokenList implements Iterable<Token> {
    private final List<Token> tokens = new LinkedList<>();
    private final ListIterator<Token> iterator = tokens.listIterator();
    private Token current = null;

    public TokenList() {}

    public void restart() {
        while (iterator.hasPrevious()) {
            iterator.previous();
        }
        current = null;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Token next() {
        try {
            current = iterator.next();
            return current;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public Token getCurrent() {
        return current;
    }

    public void append(Token token) {
        tokens.add(token);
        restart();
    }

    public void printTo(PrintStream out) {
        for (Token token : tokens) {
            out.println(token.typeName() + " " + token.getContent());
        }
    }

    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }
}
