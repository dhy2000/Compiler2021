package frontend.syntax.tree.expr.unary;

import frontend.lexical.token.IntConst;
import frontend.syntax.tree.Component;

import java.io.PrintStream;

public class Number implements Component {

    private final IntConst value;

    public Number(IntConst value) {
        this.value = value;
    }

    public IntConst getValue() {
        return value;
    }

    @Override
    public void output(PrintStream ps) {
        value.output(ps);
        ps.println("<Number>");
        ps.println("<PrimaryExp>");
    }
}
