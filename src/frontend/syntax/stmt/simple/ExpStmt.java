package frontend.syntax.stmt.simple;

import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;

import java.io.PrintStream;

public class ExpStmt implements SplStmt {

    private final Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output(PrintStream ps) {
        exp.output(ps);
    }

    @Override
    public int lineNumber() {
        BaseUnaryExp base = exp.getAddExp().getFirst().getFirst().getBase();
        if (base instanceof FunctionCall) {
            return ((FunctionCall) base).getName().lineNumber();
        } else if (base instanceof PrimaryExp) {
            BasePrimaryExp primary = ((PrimaryExp) base).getBase();
            if (primary instanceof SubExp) {
                return ((SubExp) primary).getLeftParenthesis().lineNumber();
            } else if (primary instanceof LVal) {
                return ((LVal) primary).getName().lineNumber();
            } else if (primary instanceof Number) {
                return ((Number) primary).getValue().lineNumber();
            } else {
                throw new AssertionError("PrimaryExp wrong type!");
            }
        } else {
            throw new AssertionError("UnaryExp Wrong Type");
        }
    }
}
