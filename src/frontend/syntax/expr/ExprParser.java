package frontend.syntax.expr;

import frontend.exceptions.syntax.InvalidSyntaxException;
import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.IntConst;
import frontend.lexical.token.Token;
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
    public LVal parseLVal(Ident ident) throws InvalidSyntaxException {
        final String syntax = "<LVal>";
        List<LVal.Index> indexes = new LinkedList<>();
        while (iterator.hasNext()) {
            Token left = iterator.next();
            if (!Token.Type.LBRACK.equals(left.getType())) {
                throw new InvalidSyntaxException(left.lineNumber(), syntax, left.getContent());
            }
            Exp exp = parseExp();
            // TODO: Missing Right Bracket
            if (!iterator.hasNext()) {
                throw new InvalidSyntaxException(left.lineNumber(), syntax, "EOF Missing Right ')'");
            }
            Token right = iterator.next();
            if (!Token.Type.RBRACK.equals(right.getType())) {
                throw new InvalidSyntaxException(left.lineNumber(), syntax, right.getContent());
            }
            indexes.add(new LVal.Index(left, right, exp));
        }
        return new LVal(ident, indexes);
    }

    // <SubExp>         := '(' <Exp> ')'
    public SubExp parseSubExp(Token leftParenthesis) throws InvalidSyntaxException {
        final String syntax = "<SubExp>";
        Exp exp = parseExp();
        // TODO: Missing Right Parenthesis
        if (!iterator.hasNext()) {
            throw new InvalidSyntaxException(leftParenthesis.lineNumber(), syntax, "EOF Missing Right ')'");
        }
        Token right = iterator.next();
        if (!Token.Type.RPARENT.equals(right.getType())) {
            throw new InvalidSyntaxException(right.lineNumber(), syntax, right.getContent());
        }
        return new SubExp(leftParenthesis, right, exp);
    }

    // <Number>         := IntConst
    public Number parseNumber(IntConst number) {
        return new Number(number);
    }

    // <PrimaryExp>     := <SubExp> | <LVal> | <Number> // Look forward: '(' :: <SubExp>, <Ident> :: <LVal>, <IntConst> :: <Number>
    public PrimaryExp parsePrimaryExp(Token first) throws InvalidSyntaxException {
        final String syntax = "<PrimaryExp>";
        if (!iterator.hasNext()) {
            throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
        }
        switch (first.getType()) {
            case IDENFR: return parseLVal((Ident) first);
            case INTCON: return parseNumber((IntConst) first);
            case LPARENT: return parseSubExp(first);
            default: throw new InvalidSyntaxException(first.lineNumber(), syntax, first.getContent());
        }
    }

    // <FunctionCall>   := <Ident> '(' [ <FuncRParams> ] ')' // Look forward for <FuncRParams>
    private FunctionCall parseFunctionCall(Ident ident, Token leftParenthesis) throws InvalidSyntaxException {
        final String syntax = "<UnaryExp>";
        if (!iterator.hasNext()) {
            throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
        }
        Token next = iterator.next();
        if (Token.Type.RPARENT.equals(next.getType())) {
            return new FunctionCall(ident, leftParenthesis, next);
        }
        iterator.previous();    // undo get token
        FuncRParams rParams = parseFuncRParams(parseExp());
        return new FunctionCall(ident, leftParenthesis, next, rParams);
    }

    // <FuncRParams>    := <Exp> { ',', <Exp> } // List<Exp>
    public FuncRParams parseFuncRParams(Exp first) throws InvalidSyntaxException {
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
                throw new InvalidSyntaxException(maxLineNum, syntax, "EOF after comma");
            }
            Exp exp = parseExp();
            params.add(exp);
            commas.add(next);
        }
        return new FuncRParams(first, commas, params);
    }

    // <BaseUnaryExp>   := <PrimaryExp> | <FunctionCall> // Look forward: Ident '(' :: <FunctionCall>, Ident :: <LVal>, '(' :: <SubExp>, IntConst :: <Number>
    private BaseUnaryExp parseBaseUnaryExp() throws InvalidSyntaxException {
        final String syntax = "<UnaryExp>";
        if (!iterator.hasNext()) {
            throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
        }
        Token next = iterator.next();
        if (Token.Type.IDENFR.equals(next.getType())) {
            // need one more forward
            if (!iterator.hasNext()) {
                throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
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
    public UnaryExp parseUnaryExp() throws InvalidSyntaxException {
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
    public MulExp parseMulExp() throws InvalidSyntaxException {
        final String syntax = "<MulExp>";
        UnaryExp first = parseUnaryExp();
        List<Token> operators = new LinkedList<>();
        List<UnaryExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.MULT.equals(op.getType()) || Token.Type.DIV.equals(op.getType()) || Token.Type.MOD.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                UnaryExp next = parseUnaryExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new MulExp(first, operators, operands);
    }

    // <AddExp>         := <MulExp> { ('+' | '-') <MulExp> }
    public AddExp parseAddExp() throws InvalidSyntaxException {
        final String syntax = "<AddExp>";
        MulExp first = parseMulExp();
        List<Token> operators = new LinkedList<>();
        List<MulExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                MulExp next = parseMulExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new AddExp(first, operators, operands);
    }

    // <RelExp>         := <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
    public RelExp parseRelExp() throws InvalidSyntaxException {
        final String syntax = "<RelExp>";
        AddExp first = parseAddExp();
        List<Token> operators = new LinkedList<>();
        List<AddExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                AddExp next = parseAddExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new RelExp(first, operators, operands);
    }

    // <EqExp>          := <RelExp> { ('==' | '!=') <RelExp> }
    public EqExp parseEqExp() throws InvalidSyntaxException {
        final String syntax = "<EqExp>";
        RelExp first = parseRelExp();
        List<Token> operators = new LinkedList<>();
        List<RelExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                RelExp next = parseRelExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new EqExp(first, operators, operands);
    }

    // <LAndExp>        := <EqExp> { '&&' <EqExp> }
    public LAndExp parseLAndExp() throws InvalidSyntaxException {
        final String syntax = "<LAndExp>";
        EqExp first = parseEqExp();
        List<Token> operators = new LinkedList<>();
        List<EqExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                EqExp next = parseEqExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new LAndExp(first, operators, operands);
    }

    // <LOrExp>         := <LAndExp> { '||' <LAndExp> }
    public LOrExp parseLOrExp() throws InvalidSyntaxException {
        final String syntax = "<LOrExp>";
        LAndExp first = parseLAndExp();
        List<Token> operators = new LinkedList<>();
        List<LAndExp> operands = new LinkedList<>();
        while (iterator.hasNext()) {
            Token op = iterator.next();
            if (Token.Type.PLUS.equals(op.getType()) || Token.Type.MINU.equals(op.getType())) {
                if (!iterator.hasNext()) {
                    throw new InvalidSyntaxException(maxLineNum, syntax, "EOF");
                }
                LAndExp next = parseLAndExp();
                operators.add(op);
                operands.add(next);
            } else {
                iterator.previous();
            }
        }
        return new LOrExp(first, operators, operands);
    }

    // <Exp>            := <AddExp>
    public Exp parseExp() throws InvalidSyntaxException {
        return new Exp(parseAddExp());
    }

    // <Cond>           := <LOrExp>
    public Cond parseCond() throws InvalidSyntaxException {
        return new Cond(parseLOrExp());
    }

    // <ConstExp>       := <AddExp>
    public ConstExp parseConstExp() throws InvalidSyntaxException {
        return new ConstExp(parseAddExp());
    }
}
