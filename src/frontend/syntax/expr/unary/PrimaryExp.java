package frontend.syntax.expr.unary;

import java.io.PrintStream;

public class PrimaryExp implements BaseUnaryExp {

    private final BasePrimaryExp base;

    public PrimaryExp(BasePrimaryExp base) {
        this.base = base;
    }

    public BasePrimaryExp getBase() {
        return base;
    }

    @Override
    public void output(PrintStream ps) {
        base.output(ps);
        ps.println("<PrimaryExp>");
    }
}
