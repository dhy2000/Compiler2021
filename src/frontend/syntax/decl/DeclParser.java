package frontend.syntax.decl;

import exception.UnexpectedEofException;
import exception.UnexpectedTokenException;
import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.ParserUtil;
import frontend.syntax.expr.ExprParser;
import frontend.syntax.expr.multi.ConstExp;
import frontend.syntax.expr.multi.Exp;

import java.util.*;

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
    public Decl parseDecl(Token first, Token second) throws UnexpectedTokenException, UnexpectedEofException {
        Token constTk = null;
        boolean constant = false;
        Token bType;
        Ident ident;
        String syntax;
        if (first.getType().equals(Token.Type.CONSTTK) && second.getType().equals(Token.Type.INTTK)) {
            syntax = "<ConstDecl>";
            constant = true;
            constTk = first;
            bType = second;
            ident = (Ident) ParserUtil.getSpecifiedToken(Token.Type.IDENFR, "<ConstDecl>", iterator, maxLineNum);
        } else if (first.getType().equals(Token.Type.INTTK) && second.getType().equals(Token.Type.IDENFR)) {
            syntax = "<VarDecl>";
            bType = first;
            ident = (Ident) second;
        } else {
            throw new UnexpectedTokenException(first.lineNumber(), "<Decl>", first);
        }

        Def firstDef = parseDef(constant, ident);
        List<Token> commas = new LinkedList<>();
        List<Def> followDefs = new LinkedList<>();
        Token semi = null;
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.SEMICN)) {
                semi = next;
                break;
            } else if (!next.getType().equals(Token.Type.COMMA)) {
                break; // missing semicolon
            }
            Ident nextIdent = (Ident) ParserUtil.getSpecifiedToken(Token.Type.IDENFR, syntax, iterator, maxLineNum);
            Def def = parseDef(constant, nextIdent);
            commas.add(next);
            followDefs.add(def);
        }
        if (Objects.isNull(semi)) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        if (constant) {
            return new Decl(constTk, bType, firstDef, commas, followDefs, semi);
        } else {
            return new Decl(bType, firstDef, commas, followDefs, semi);
        }
    }

    // <ArrDef>        := '[' <ConstExp> ']'
    public Def.ArrDef parseArrDef(Token leftBracket) throws UnexpectedTokenException, UnexpectedEofException {
        ConstExp constExp = new ExprParser(iterator, maxLineNum).parseConstExp();
        Token rightBracket = ParserUtil.getNullableToken(Token.Type.RBRACK, "<ArrDef>", iterator, maxLineNum);
        return new Def.ArrDef(leftBracket, rightBracket, constExp);
    }

    // <Def>           := Ident { <ArrayDef> } [ '=' <InitVal> ]
    public Def parseDef(boolean constant, Ident ident) throws UnexpectedTokenException, UnexpectedEofException {
        List<Def.ArrDef> arrDefs = new LinkedList<>();
        // arrayDefs
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.LBRACK)) {
                arrDefs.add(parseArrDef(next));
            } else {
                iterator.previous();
                break;
            }
        }
        Token assignTk = iterator.next();
        if (assignTk.getType().equals(Token.Type.ASSIGN)) {
            InitVal initVal = parseInitVal(constant);
            return new Def(constant, ident, arrDefs, assignTk, initVal);
        } else {
            iterator.previous();
            assert !constant;
            return new Def(ident, arrDefs);
        }
    }

    // <ExpInitVal>    := <Exp>
    public ExpInitVal parseExpInitVal(boolean constant) throws UnexpectedTokenException, UnexpectedEofException {
        if (constant) {
            ConstExp constExp = new ExprParser(iterator, maxLineNum).parseConstExp();
            return new ExpInitVal(true, constExp);
        } else {
            Exp exp = new ExprParser(iterator, maxLineNum).parseExp();
            return new ExpInitVal(false, exp);
        }
    }

    // <ArrInitVal>    := '{' [ <InitVal> { ',' <InitVal> } ] '}'
    public ArrInitVal parseArrInitVal(boolean constant, Token leftBrace) throws UnexpectedTokenException, UnexpectedEofException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, "<ArrInitVal>");
        }
        Token rightBrace = iterator.next();
        if (rightBrace.getType().equals(Token.Type.RBRACE)) {
            return new ArrInitVal(constant, leftBrace, rightBrace);
        }
        iterator.previous();
        InitVal firstVal = parseInitVal(constant);
        List<Token> commas = new LinkedList<>();
        List<InitVal> followVal = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.RBRACE)) {
                rightBrace = next;
                break;
            } else if (next.getType().equals(Token.Type.COMMA)) {
                commas.add(next);
                followVal.add(parseInitVal(constant));
            } else {
                throw new UnexpectedTokenException(next.lineNumber(), "<ArrInitVal>", next);
            }
        }
        return new ArrInitVal(constant, leftBrace, rightBrace, firstVal, commas, followVal);
    }

    // <InitVal>       := <ExpInitVal> | <ArrInitVal>
    public InitVal parseInitVal(boolean constant) throws UnexpectedEofException, UnexpectedTokenException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, "<InitVal>");
        }
        Token next = iterator.next();
        if (next.getType().equals(Token.Type.LBRACE)) {
            return parseArrInitVal(constant, next);
        } else {
            iterator.previous();
            return parseExpInitVal(constant);
        }
    }
}
