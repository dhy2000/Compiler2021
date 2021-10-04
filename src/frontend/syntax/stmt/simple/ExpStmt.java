package frontend.syntax.stmt.simple;

import frontend.syntax.expr.multi.Exp;

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
}
