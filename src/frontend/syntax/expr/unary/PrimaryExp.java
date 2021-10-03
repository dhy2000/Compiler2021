package frontend.syntax.expr.unary;

import java.io.PrintStream;

public interface PrimaryExp extends BaseUnaryExp {
    @Override
    default void output(PrintStream ps) {
        ps.println("<PrimaryExp>");
    }
}
