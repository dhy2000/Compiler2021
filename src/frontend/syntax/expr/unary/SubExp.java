package frontend.syntax.expr.unary;

import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;

public class SubExp implements BasePrimaryExp {

    private final Token leftParenthesis;
    private final Token rightParenthesis;
    private final Exp exp;

    public SubExp(Token leftParenthesis, Token rightParenthesis, Exp exp) {
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.exp = exp;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output(PrintStream ps) {
        leftParenthesis.output(ps);
        exp.output(ps);
        rightParenthesis.output(ps);
    }
}
