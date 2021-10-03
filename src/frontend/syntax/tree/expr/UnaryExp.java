package frontend.syntax.tree.expr;

import frontend.syntax.tree.Component;

import java.io.PrintStream;

public class UnaryExp implements Component {
    @Override
    public void output(PrintStream ps) {
        // traverse children
        ps.println("<UnaryExp>");
    }
}
