package frontend.syntax;

import frontend.lexical.TokenList;
import frontend.lexical.token.Token;

import java.util.ListIterator;

public class CompUnitParser {

    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public CompUnitParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public CompUnitParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    public CompUnit parseCompUnit() {
        return null;
    }
}
