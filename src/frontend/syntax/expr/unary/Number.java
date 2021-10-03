package frontend.syntax.expr.unary;

import frontend.lexical.token.IntConst;

import java.io.PrintStream;

public class Number implements PrimaryExp {

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
