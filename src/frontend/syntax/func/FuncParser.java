package frontend.syntax.func;

import frontend.error.exception.syntax.UnexpectedEofException;
import frontend.error.exception.syntax.UnexpectedTokenException;
import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.ParserUtil;
import frontend.syntax.expr.ExprParser;
import frontend.syntax.expr.multi.ConstExp;
import frontend.syntax.stmt.StmtParser;
import frontend.syntax.stmt.complex.Block;

import java.util.LinkedList;
import java.util.List;
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
    public FuncDef parseFuncDef(Token funcType, Ident ident, Token leftParenthesis) throws UnexpectedEofException, UnexpectedTokenException {
        ParserUtil.detectEof("<FuncDef>", iterator, maxLineNum);
        Token rightParenthesis = iterator.next();
        if (rightParenthesis.getType().equals(Token.Type.RPARENT)) {
            Token leftBrace = ParserUtil.getSpecifiedToken(Token.Type.LBRACE, "<Block>", iterator, maxLineNum);
            Block body = new StmtParser(iterator, maxLineNum).parseBlock(leftBrace);
            return new FuncDef(funcType, ident, leftParenthesis, rightParenthesis, body);
        } else {
            iterator.previous();
            FuncFParams fParams = parseFuncFParams();
            rightParenthesis = ParserUtil.getSpecifiedToken(Token.Type.RPARENT, "<FuncDef>", iterator, maxLineNum);
            Token leftBrace = ParserUtil.getSpecifiedToken(Token.Type.LBRACE, "<Block>", iterator, maxLineNum);
            Block body = new StmtParser(iterator, maxLineNum).parseBlock(leftBrace);
            return new FuncDef(funcType, ident, leftParenthesis, rightParenthesis, fParams, body);
        }
    }

    // <MainFuncDef>   := 'int' 'main' '(' ')' <Block>
    public MainFuncDef parseMainFuncDef(Token intTk, Token mainTk, Token leftParenthesis) throws UnexpectedTokenException, UnexpectedEofException {
        Token rightParenthesis = ParserUtil.getSpecifiedToken(Token.Type.RPARENT, "<MainFuncDef>", iterator, maxLineNum);
        Token leftBrace = ParserUtil.getSpecifiedToken(Token.Type.LBRACE, "<Block>", iterator, maxLineNum);
        Block body = new StmtParser(iterator, maxLineNum).parseBlock(leftBrace);
        return new MainFuncDef(intTk, mainTk, leftParenthesis, rightParenthesis, body);
    }

    // <FuncFParams>   := <FuncFParam> { ',' <FuncFParam> }
    public FuncFParams parseFuncFParams() throws UnexpectedTokenException, UnexpectedEofException {
        FuncFParam first = parseFuncFParam();
        List<Token> commas = new LinkedList<>();
        List<FuncFParam> followParam = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.COMMA)) {
                commas.add(next);
                followParam.add(parseFuncFParam());
            } else {
                iterator.previous();
                break;
            }
        }
        return new FuncFParams(first, commas, followParam);
    }

    // <FuncFParam>    := <BType> Ident [ '[' ']' { '[' <ConstExp> ']' } ]
    public FuncFParam parseFuncFParam() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<FuncFParam>";
        Token bType = ParserUtil.getSpecifiedToken(Token.Type.INTTK, syntax, iterator, maxLineNum);
        Ident ident = (Ident) ParserUtil.getSpecifiedToken(Token.Type.IDENFR, syntax, iterator, maxLineNum);
        if (!iterator.hasNext()) {
            return new FuncFParam(bType, ident);
        }
        Token leftBracket = iterator.next();
        if (!leftBracket.getType().equals(Token.Type.LBRACK)) {
            iterator.previous();
            return new FuncFParam(bType, ident);
        }
        Token rightBracket = ParserUtil.getSpecifiedToken(Token.Type.RBRACK, syntax, iterator, maxLineNum);
        if (!iterator.hasNext()) {
            return new FuncFParam(bType, ident, leftBracket, rightBracket);
        }
        List<FuncFParam.ArrayDim> arrayDims = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.LBRACK)) {
                arrayDims.add(parseArrayDim(next));
            } else {
                iterator.previous();
                break;
            }
        }
        return new FuncFParam(bType, ident, leftBracket, rightBracket, arrayDims);
    }

    // <ArrayDim>   := '[' <ConstExp> ']'
    public FuncFParam.ArrayDim parseArrayDim(Token leftBracket) throws UnexpectedTokenException, UnexpectedEofException {
        ConstExp constExp = new ExprParser(iterator, maxLineNum).parseConstExp();
        Token rightBracket = ParserUtil.getSpecifiedToken(Token.Type.RBRACK, "<FuncFParam>", iterator, maxLineNum);
        return new FuncFParam.ArrayDim(leftBracket, rightBracket, constExp);
    }
}
