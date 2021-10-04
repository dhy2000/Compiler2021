package frontend.syntax.func;

import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;

import java.util.ListIterator;

public class FuncParser {
    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public FuncParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public FuncParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    public FuncDef parseFuncDef(Token funcType, Ident ident, Token leftParenthesis) {
        return null;
    }

    public MainFuncDef parseMainFuncDef(Token intTk, Token mainTk, Token leftParenthesis) {
        return null;
    }

    public FuncFParams parseFuncFParams() {
        return null;
    }

    public FuncFParam parseFuncFParam() {
        return null;
    }
}
