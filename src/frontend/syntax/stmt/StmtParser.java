package frontend.syntax.stmt;

import exception.UnexpectedEofException;
import exception.UnexpectedTokenException;
import frontend.lexical.TokenList;
import frontend.lexical.token.FormatString;
import frontend.lexical.token.Token;
import frontend.syntax.ParserUtil;
import frontend.syntax.decl.DeclParser;
import frontend.syntax.expr.ExprParser;
import frontend.syntax.expr.multi.AddExp;
import frontend.syntax.expr.multi.Cond;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MulExp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.stmt.complex.*;
import frontend.syntax.stmt.simple.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class StmtParser {
    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public StmtParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public StmtParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    // <AssignStmt>     := <LVal> '=' <Exp>
    public AssignStmt parseAssignStmt(LVal target, Token assignTk) throws UnexpectedTokenException, UnexpectedEofException {
        Exp exp = new ExprParser(iterator, maxLineNum).parseExp();
        return new AssignStmt(target, assignTk, exp);
    }

    // <ExpStmt>       := <Exp>
    public ExpStmt parseExpStmt(Exp exp) {
        return new ExpStmt(exp);
    }

    // <BreakStmt>     := 'break'
    public BreakStmt parseBreakStmt(Token breakTk) {
        assert breakTk.getType().equals(Token.Type.BREAKTK);
        return new BreakStmt(breakTk);
    }

    // <ContinueStmt>  := 'continue'
    public ContinueStmt parseContinueStmt(Token continueTk) {
        assert continueTk.getType().equals(Token.Type.CONTINUETK);
        return new ContinueStmt(continueTk);
    }

    // <ReturnStmt>    := 'return' [<Exp>]
    public ReturnStmt parseReturnStmt(Token returnTk) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<ReturnStmt>";
        // forward 1 whether is ';'
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(returnTk.lineNumber(), syntax);
        }
        Token semi = iterator.next();
        if (semi.getType().equals(Token.Type.SEMICN)) {
            iterator.previous();
            return new ReturnStmt(returnTk);
        } else {
            iterator.previous();
            Exp exp = new ExprParser(iterator, maxLineNum).parseExp();
            return new ReturnStmt(returnTk, exp);
        }
    }

    // <InputStmt>     := <LVal> '=' 'getint' '(' ')'
    public InputStmt parseInputStmt(LVal target, Token assignTk, Token getIntTk) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<InputStmt>";
        Token leftParenthesis = ParserUtil.getSpecifiedToken(Token.Type.LPARENT, syntax, iterator, maxLineNum);
        Token rightParenthesis = ParserUtil.getNullableToken(Token.Type.RPARENT, syntax, iterator, maxLineNum);
        return new InputStmt(target, assignTk, getIntTk, leftParenthesis, rightParenthesis);
    }

    // <OutputStmt>    := 'printf' '(' FormatString { ',' <Exp> } ')'
    public OutputStmt parseOutputStmt(Token printfTk) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<OutputStmt>";
        Token leftParenthesis = ParserUtil.getSpecifiedToken(Token.Type.LPARENT, syntax, iterator, maxLineNum);
        Token formatString = ParserUtil.getSpecifiedToken(Token.Type.STRCON, syntax, iterator, maxLineNum);
        List<Token> commas = new LinkedList<>();
        List<Exp> exps = new LinkedList<>();
        while (iterator.hasNext()) {
            Token comma = iterator.next();
            if (!comma.getType().equals(Token.Type.COMMA)) {
                iterator.previous();
                break;
            }
            commas.add(comma);
            Exp exp = new ExprParser(iterator, maxLineNum).parseExp();
            exps.add(exp);
        }
        Token rightParenthesis = ParserUtil.getNullableToken(Token.Type.RPARENT, syntax, iterator, maxLineNum);
        return new OutputStmt(printfTk, leftParenthesis, rightParenthesis, (FormatString) formatString, commas, exps);
    }

    private LVal extractLValFromExp(Exp exp) {
        AddExp addExp = exp.getAddExp();
        MulExp mulExp = addExp.getFirst();
        UnaryExp unaryExp = mulExp.getFirst();
        BaseUnaryExp baseUnaryExp = unaryExp.getBase();
        if (!(baseUnaryExp instanceof PrimaryExp)) {
            return null;
        }
        PrimaryExp primaryExp = (PrimaryExp) baseUnaryExp;
        BasePrimaryExp base = primaryExp.getBase();
        if (base instanceof LVal) {
            if (addExp.size() == 0 && mulExp.size() == 0 && unaryExp.sizeUnaryOp() == 0) { return (LVal) base; }
            else return null;
        }
        return null;
    }

    // <SplStmt>      := <AssignStmt> | <ExpStmt> | <BreakStmt> | <ContinueStmt> | <ReturnStmt> | <InputStmt> | <OutputStmt>
    public SplStmt parseSimpleStmt(Token first) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<SplStmt>";
        switch (first.getType()) {
            case BREAKTK: return parseBreakStmt(first);
            case CONTINUETK: return parseContinueStmt(first);
            case RETURNTK: return parseReturnStmt(first);
            case PRINTFTK: return parseOutputStmt(first);
            default:
        }
        // fall through
        iterator.previous(); // roll back the first token
        Exp exp = new ExprParser(iterator, maxLineNum).parseExp();
        // check whether is lVal


        LVal target = extractLValFromExp(exp);
        if (Objects.nonNull(target)) {
            // is lVal
            if (!iterator.hasNext()) {
                throw new UnexpectedEofException(maxLineNum, syntax);
            }
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.ASSIGN)) {
                // assign or input
                if (!iterator.hasNext()) {
                    throw new UnexpectedEofException(maxLineNum, syntax);
                }
                Token twice = iterator.next();
                if (twice.getType().equals(Token.Type.GETINTTK)) {
                    return parseInputStmt(target, next, twice);
                } else {
                    iterator.previous();
                    return parseAssignStmt(target, next);
                }
            } else {
                iterator.previous();
                // fall through
            }
        }
        return parseExpStmt(exp);
    }

    // <IfStmt>        := 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
    public IfStmt parseIfStmt(Token ifTk) throws UnexpectedTokenException, UnexpectedEofException {
        assert ifTk.getType().equals(Token.Type.IFTK);
        final String syntax = "<IfStmt>";
        Token leftParenthesis = ParserUtil.getSpecifiedToken(Token.Type.LPARENT, syntax, iterator, maxLineNum);
        Cond cond = new ExprParser(iterator, maxLineNum).parseCond();
        Token rightParenthesis = ParserUtil.getNullableToken(Token.Type.RPARENT, syntax, iterator, maxLineNum);
        Stmt thenStmt = parseStmt();
        Token elseTk;
        if (iterator.hasNext() && (elseTk = iterator.next()).getType().equals(Token.Type.ELSETK)) {
            Stmt elseStmt = parseStmt();
            return new IfStmt(ifTk, leftParenthesis, cond, rightParenthesis, thenStmt, elseTk, elseStmt);
        } else {
            iterator.previous();
        }
        return new IfStmt(ifTk, leftParenthesis, cond, rightParenthesis, thenStmt);
    }

    // <WhileStmt>     := 'while' '(' <Cond> ')' <Stmt>
    public WhileStmt parseWhileStmt(Token whileTk) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<WhileStmt>";
        Token leftParenthesis = ParserUtil.getSpecifiedToken(Token.Type.LPARENT, syntax, iterator, maxLineNum);
        Cond cond = new ExprParser(iterator, maxLineNum).parseCond();
        Token rightParenthesis = ParserUtil.getNullableToken(Token.Type.RPARENT, syntax, iterator, maxLineNum);
        Stmt stmt = parseStmt();
        return new WhileStmt(whileTk, leftParenthesis, cond, rightParenthesis, stmt);
    }

    // <CplStmt>       := <BranchStmt> | <LoopStmt> | <Block>
    public CplStmt parseComplexStmt(Token first) throws UnexpectedTokenException, UnexpectedEofException {
        switch (first.getType()) {
            case IFTK: return parseIfStmt(first);
            case WHILETK: return parseWhileStmt(first);
            case LBRACE: return parseBlock(first);
        }
        throw new UnexpectedTokenException(first.lineNumber(), "<CplStmt>", first);
    }

    // <BlockItem>     := <Decl> | <Stmt>
    public BlockItem parseBlockItem() throws UnexpectedEofException, UnexpectedTokenException {
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, "<BlockItem>");
        }
        Token next = iterator.next();
        if (next.getType().equals(Token.Type.INTTK)) {
            Token second = ParserUtil.getSpecifiedToken(Token.Type.IDENFR, "<Decl>", iterator, maxLineNum);
            return new DeclParser(iterator, maxLineNum).parseDecl(next, second);
        } else if (next.getType().equals(Token.Type.CONSTTK)) {
            Token second = ParserUtil.getSpecifiedToken(Token.Type.INTTK, "<Decl>", iterator, maxLineNum);
            return new DeclParser(iterator, maxLineNum).parseDecl(next, second);
        } else {
            iterator.previous();
            return parseStmt();
        }
    }

    // <Block>         := '{' { <BlockItem> } '}'
    public Block parseBlock(Token leftBrace) throws UnexpectedTokenException, UnexpectedEofException {
        List<BlockItem> blockItems = new LinkedList<>();
        while (iterator.hasNext()) {
            Token end = iterator.next();
            if (end.getType().equals(Token.Type.RBRACE)) {
                return new Block(leftBrace, end, blockItems);
            }
            iterator.previous();
            BlockItem item = parseBlockItem();
            blockItems.add(item);
        }
        throw new UnexpectedEofException(maxLineNum, "<Block>");
    }

    // <Stmt>          := ';' | <SplStmt> ';' | <CplStmt>
    public Stmt parseStmt() throws UnexpectedEofException, UnexpectedTokenException {
        final String syntax = "<Stmt>";
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        Token first = iterator.next();
        if (first.getType().equals(Token.Type.SEMICN)) {
            return new Stmt(first); // empty stmt
        } else if (first.getType().equals(Token.Type.LBRACE) || first.getType().equals(Token.Type.IFTK) || first.getType().equals(Token.Type.WHILETK)) {
            CplStmt cplStmt = parseComplexStmt(first);
            return new Stmt(cplStmt);
        } else {
            SplStmt splStmt = parseSimpleStmt(first);
            Token semi = ParserUtil.getNullableToken(Token.Type.SEMICN, syntax, iterator, maxLineNum);
            return new Stmt(splStmt, semi);
        }
    }

}
