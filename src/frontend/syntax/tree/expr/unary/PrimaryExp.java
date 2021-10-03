package frontend.syntax.tree.expr.unary;

import java.io.PrintStream;

public interface PrimaryExp extends BaseUnaryExp {
    @Override
    default void output(PrintStream ps) {
        ps.println("<PrimaryExp>");
    }
}
