package com.compiler.model.token;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TokenStream {
    private final List<Token> tokens = new LinkedList<>();
    private final ListIterator<Token> iterator = tokens.listIterator();
    private Token current = null;

    public TokenStream() {}

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
    }
}
