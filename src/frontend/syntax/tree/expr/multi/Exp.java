package frontend.syntax.tree.expr.multi;

import frontend.syntax.tree.Component;

import java.io.PrintStream;

public class Exp implements Component {
    private final AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public void output(PrintStream ps) {
        addExp.output(ps);
        ps.println("<Exp>");
    }
}
