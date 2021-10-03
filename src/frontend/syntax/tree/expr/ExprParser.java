package frontend.syntax.tree.expr;

import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.IntConst;
import frontend.lexical.token.Token;
import frontend.syntax.tree.expr.multi.*;
import frontend.syntax.tree.expr.unary.*;

import java.lang.Number;
import java.util.ListIterator;

/**
 * 表达式相关的语法分析器
 */
public class ExprParser {

    private final ListIterator<Token> iterator;

    public ExprParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
    }

    // <LVal>           := Ident { '[' <Exp> ']' }
    public LVal parseLVal(Ident ident) {
        return null;
    }

    // <SubExp>         := '(' <Exp> ')'
    public SubExp parseSubExp(Token leftParenthesis) {
        return null;
    }

    // <Number>         := IntConst
    public Number parseNumber(IntConst number) {
        return null;
    }

    // <PrimaryExp>     := <SubExp> | <LVal> | <Number> // Look forward: '(' :: <SubExp>, <Ident> :: <LVal>, <IntConst> :: <Number>
    public PrimaryExp parsePrimaryExp() {
        return null;
    }

    // <FunctionCall>   := <Ident> '(' [ <FuncRParams ] ')'
    private FunctionCall parseFunctionCall(Ident ident, Token leftParenthesis) {
        return null;
    }

    // <FuncRParams>    := <Exp> { ',', <Exp> } // List<Exp>
    public FuncRParams parseFuncRParams() {
        return null;
    }

    // <BaseUnaryExp>   := <PrimaryExp> | <FunctionCall> // Look forward: Ident '(' :: <FunctionCall>, Ident :: <LVal>, '(' :: <SubExp>, IntConst :: <Number>
    private BaseUnaryExp parseBaseUnaryExp() {
        return null;
    }

    // <UnaryExp>       := { <UnaryOp> } <BasicUnaryExp>
    public UnaryExp parseUnaryExp() {
        return null;
    }

    // <MulExp>         := <UnaryExp> { ('*' | '/' | '%') <UnaryExp> }
    public MulExp parseMulExp() {
        return null;
    }

    // <AddExp>         := <MulExp> { ('+' | '-') <MulExp> }
    public AddExp parseAddExp() {
        return null;
    }

    // <RelExp>         := <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
    public RelExp parseRelExp() {
        return null;
    }

    // <EqExp>          := <RelExp> { ('==' | '!=') <RelExp> }
    public EqExp parseEqExp() {
        return null;
    }

    // <LAndExp>        := <EqExp> { '&&' <EqExp> }
    public LAndExp parseLAndExp() {
        return null;
    }

    // <LOrExp>         := <LAndExp> { '||' <LAndExp> }
    public LOrExp parseLOrExp() {
        return null;
    }

    // <Exp>            := <AddExp>
    public Exp parseExp() {
        return null;
    }

    // <Cond>           := <LOrExp>
    public Cond parseCond() {
        return null;
    }
    // <ConstExp>       := <AddExp>
    public ConstExp parseConstExp() {
        return null;
    }
}
