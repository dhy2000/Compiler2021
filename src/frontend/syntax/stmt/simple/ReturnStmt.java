package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;
import java.util.Objects;

public class ReturnStmt implements SplStmt {

    private final Token returnTk;
    private final Exp value;

    public ReturnStmt(Token returnTk) {
        assert Token.Type.RETURNTK.equals(returnTk.getType());
        this.returnTk = returnTk;
        this.value = null;
    }

    public ReturnStmt(Token returnTk, Exp value) {
        assert Token.Type.RETURNTK.equals(returnTk.getType());
        this.returnTk = returnTk;
        this.value = value;
    }

    public Token getReturnTk() {
        return returnTk;
    }

    public boolean hasValue() {
        return Objects.nonNull(value);
    }

    public Exp getValue() {
        return value;
    }

    @Override
    public void output(PrintStream ps) {
        returnTk.output(ps);
        if (Objects.nonNull(value)) {
            value.output(ps);
        }
    }
}
