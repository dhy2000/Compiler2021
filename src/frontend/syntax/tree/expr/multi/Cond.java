package frontend.syntax.tree.expr.multi;

import frontend.syntax.tree.Component;

import java.io.PrintStream;

public class Cond implements Component {

    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    @Override
    public void output(PrintStream ps) {
        lOrExp.output(ps);
        ps.println("<Cond>");
    }
}
