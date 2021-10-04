package frontend.syntax.decl;

import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;

import java.util.ListIterator;

public class DeclParser {
    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public DeclParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public DeclParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    // <Decl>          := ['const'] <BType> <Def> { ',' <Def> } ';'
    public Decl parseDecl(Token first, Token second, Token third) {
        return null;
    }

    // <ArrDef>        := '[' <ConstExp> ']'
    public Def.ArrDef parseArrDef(Token leftBracket) {
        return null;
    }

    // <Def>           := Ident { <ArrayDef> } [ '=' <InitVal> ]
    public Def parseDef(boolean constant, Ident ident) {
        return null;
    }

    // <ArrInitVal>    := '{' [ <InitVal> { ',' <InitVal> } ] '}'
    public ExpInitVal parseExpInitVal(boolean constant) {
        return null;
    }

    // <ExpInitVal>    := <Exp>
    public ArrInitVal parseArrInitVal(boolean constant) {
        return null;
    }

    // <InitVal>       := <ExpInitVal> | <ArrInitVal>
    public InitVal parseInitVal(boolean constant) {
        return null;
    }
}
