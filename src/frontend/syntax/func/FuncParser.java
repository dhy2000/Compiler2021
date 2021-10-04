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

    // <FuncDef>       := <FuncType> Ident '(' [<FuncFParams> ] ')' <Block>
    public FuncDef parseFuncDef(Token funcType, Ident ident, Token leftParenthesis) {
        return null;
    }

    // <MainFuncDef>   := 'int' 'main' '(' ')' <Block>
    public MainFuncDef parseMainFuncDef(Token intTk, Token mainTk, Token leftParenthesis) {
        return null;
    }

    // <FuncFParams>   := <FuncFParam> { ',' <FuncFParam> }
    public FuncFParams parseFuncFParams() {
        return null;
    }

    // <FuncFParam>    := <BType> Ident [ '[' ']' { '[' <ConstExp> ']' } ]
    public FuncFParam parseFuncFParam() {
        return null;
    }
}
