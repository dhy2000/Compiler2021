package frontend.syntax.expr;

import frontend.error.exception.syntax.MissingRightBracketException;
import frontend.error.exception.syntax.UnexpectedEofException;
import frontend.error.exception.syntax.UnexpectedTokenException;
import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.IntConst;
import frontend.lexical.token.Token;
import frontend.syntax.ParserUtil;
import frontend.syntax.expr.multi.*;
import frontend.syntax.expr.unary.Number;
import frontend.syntax.expr.unary.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 表达式相关的语法分析器
 */
public class ExprParser {

    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public ExprParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public ExprParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    // <LVal>           := Ident { '[' <Exp> ']' }
    public LVal parseLVal(Ident ident) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<LVal>";
        List<LVal.Index> indexes = new LinkedList<>();
        while (iterator.hasNext()) {
            Token left = iterator.next();
            if (!Token.Type.LBRACK.equals(left.getType())) {
                iterator.previous();
                break;
            }
            Exp exp = parseExp();
            // TODO: Missing Right Bracket
            if (!iterator.hasNext()) {
                MissingRightBracketException.registerError(left.lineNumber(), syntax);
                indexes.add(new LVal.Index(left, null, exp));
            } else {
                Token right = iterator.next();
                if (!Token.Type.RBRACK.equals(right.getType())) {
                    MissingRightBracketException.registerError(left.lineNumber(), syntax);
                    iterator.previous();
                    indexes.add(new LVal.Index(left, null, exp));
                    continue;
                }
                indexes.add(new LVal.Index(left, right, exp));
            }
        }
        return new LVal(ident, indexes);
    }

    // <SubExp>         := '(' <Exp> ')'
    public SubExp parseSubExp(Token leftParenthesis) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<SubExp>";
        Exp exp = parseExp();
        // TODO: Missing Right Parenthesis
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        Token right = iterator.next();
        if (!Token.Type.RPARENT.equals(right.getType())) {
            throw new UnexpectedTokenException(right.lineNumber(), syntax, right, Token.Type.RPARENT);
        }
        return new SubExp(leftParenthesis, right, exp);
    }

    // <Number>         := IntConst
    public Number parseNumber(IntConst number) {
        return new Number(number);
    }

    // <PrimaryExp>     := <SubExp> | <LVal> | <Number> // Look forward: '(' :: <SubExp>, <Ident> :: <LVal>, <IntConst> :: <Number>
    public PrimaryExp parsePrimaryExp(Token first) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<PrimaryExp>";
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        switch (first.getType()) {
            case IDENFR: return new PrimaryExp(parseLVal((Ident) first));
            case INTCON: return new PrimaryExp(parseNumber((IntConst) first));
            case LPARENT: return new PrimaryExp(parseSubExp(first));
            default: throw new UnexpectedTokenException(first.lineNumber(), syntax, first);
        }
    }

    // <FunctionCall>   := <Ident> '(' [ <FuncRParams> ] ')' // Look forward for <FuncRParams>
    private FunctionCall parseFunctionCall(Ident ident, Token leftParenthesis) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<UnaryExp>";
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        Token next = iterator.next();
        if (Token.Type.RPARENT.equals(next.getType())) {
            return new FunctionCall(ident, leftParenthesis, next);
        }
        iterator.previous();    // undo get token
        FuncRParams rParams = parseFuncRParams(parseExp());
        next = ParserUtil.getSpecifiedToken(Token.Type.RPARENT, syntax, iterator, maxLineNum);
        return new FunctionCall(ident, leftParenthesis, next, rParams);
    }

    // <FuncRParams>    := <Exp> { ',', <Exp> } // List<Exp>
    public FuncRParams parseFuncRParams(Exp first) throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<FuncRParams>";
        List<Exp> params = new LinkedList<>();
        List<Token> commas = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (!Token.Type.COMMA.equals(next.getType())) {
                iterator.previous();
                break;
            }
            if (!iterator.hasNext()) {
                throw new UnexpectedEofException(maxLineNum, syntax);
            }
            Exp exp = parseExp();
            params.add(exp);
            commas.add(next);
        }
        return new FuncRParams(first, commas, params);
    }

    // <BaseUnaryExp>   := <PrimaryExp> | <FunctionCall> // Look forward: Ident '(' :: <FunctionCall>, Ident :: <LVal>, '(' :: <SubExp>, IntConst :: <Number>
    private BaseUnaryExp parseBaseUnaryExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<UnaryExp>";
        if (!iterator.hasNext()) {
            throw new UnexpectedEofException(maxLineNum, syntax);
        }
        Token next = iterator.next();
        if (Token.Type.IDENFR.equals(next.getType())) {
            // need one more forward
            if (!iterator.hasNext()) {
                throw new UnexpectedEofException(maxLineNum, syntax);
            }
            Token two = iterator.next();
            if (Token.Type.LPARENT.equals(two.getType())) {
                return parseFunctionCall((Ident) next, two);
            }
            iterator.previous();    // undo `two`
            return parsePrimaryExp(next);
        }
        return parsePrimaryExp(next);
    }

    // <UnaryExp>       := { <UnaryOp> } <BaseUnaryExp>
    public UnaryExp parseUnaryExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<UnaryExp>";
        List<Token> unaryOps = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (Token.Type.PLUS.equals(next.getType()) || Token.Type.MINU.equals(next.getType()) || Token.Type.NOT.equals(next.getType())) {
                unaryOps.add(next);
            } else {
                break;
            }
        }
        iterator.previous(); // undo last ~unaryOp
        BaseUnaryExp unaryExp = parseBaseUnaryExp();
        return new UnaryExp(unaryOps, unaryExp);
    }

    // <MulExp>         := <UnaryExp> { ('*' | '/' | '%') <UnaryExp> }
    public MulExp parseMulExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<MulExp>";
        UnaryExp first = parseUnaryExp();
        List<Token> operators = new LinkedList<>();
        List<UnaryExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.MULT.equals(op.getType()) || Token.Type.DIV.equals(op.getType())
                    || Token.Type.MOD.equals(op.getType())) {
                UnaryExp next = parseUnaryExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new MulExp(first, operators, operands);
    }

    // <AddExp>         := <MulExp> { ('+' | '-') <MulExp> }
    public AddExp parseAddExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<AddExp>";
        MulExp first = parseMulExp();
        List<Token> operators = new LinkedList<>();
        List<MulExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                MulExp next = parseMulExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new AddExp(first, operators, operands);
    }

    // <RelExp>         := <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
    public RelExp parseRelExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<RelExp>";
        AddExp first = parseAddExp();
        List<Token> operators = new LinkedList<>();
        List<AddExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.LSS.equals(op.getType()) || Token.Type.GRE.equals(op.getType())
                    || Token.Type.LEQ.equals(op.getType()) || Token.Type.GEQ.equals(op.getType())) {
                AddExp next = parseAddExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new RelExp(first, operators, operands);
    }

    // <EqExp>          := <RelExp> { ('==' | '!=') <RelExp> }
    public EqExp parseEqExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<EqExp>";
        RelExp first = parseRelExp();
        List<Token> operators = new LinkedList<>();
        List<RelExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.EQL.equals(op.getType()) || Token.Type.NEQ.equals(op.getType())) {
                RelExp next = parseRelExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new EqExp(first, operators, operands);
    }

    // <LAndExp>        := <EqExp> { '&&' <EqExp> }
    public LAndExp parseLAndExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<LAndExp>";
        EqExp first = parseEqExp();
        List<Token> operators = new LinkedList<>();
        List<EqExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.AND.equals(op.getType())) {
                EqExp next = parseEqExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new LAndExp(first, operators, operands);
    }

    // <LOrExp>         := <LAndExp> { '||' <LAndExp> }
    public LOrExp parseLOrExp() throws UnexpectedTokenException, UnexpectedEofException {
        final String syntax = "<LOrExp>";
        LAndExp first = parseLAndExp();
        List<Token> operators = new LinkedList<>();
        List<LAndExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.OR.equals(op.getType())) {
                LAndExp next = parseLAndExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
                break;
            }
        }
        return new LOrExp(first, operators, operands);
    }

    // <Exp>            := <AddExp>
    public Exp parseExp() throws UnexpectedTokenException, UnexpectedEofException {
        return new Exp(parseAddExp());
    }

    // <Cond>           := <LOrExp>
    public Cond parseCond() throws UnexpectedTokenException, UnexpectedEofException {
        return new Cond(parseLOrExp());
    }

    // <ConstExp>       := <AddExp>
    public ConstExp parseConstExp() throws UnexpectedTokenException, UnexpectedEofException {
        return new ConstExp(parseAddExp());
    }
}
