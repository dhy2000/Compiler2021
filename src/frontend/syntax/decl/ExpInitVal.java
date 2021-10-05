package frontend.syntax.decl;

import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;

public class ExpInitVal implements InitVal {

    private final boolean constant;

    private final Exp exp;

    public ExpInitVal(boolean constant, Exp exp) {
        this.constant = constant;
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output(PrintStream ps) {
        exp.output(ps);
        if (isConst()) {
            ps.println("<ConstInitVal>");
        } else {
            ps.println("<InitVal>");
        }
    }

    @Override
    public boolean isConst() {
        return constant;
    }
}
