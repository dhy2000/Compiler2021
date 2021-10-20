package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;

import java.io.PrintStream;

public class BreakStmt implements SplStmt {

    private final Token breakTk;

    public BreakStmt(Token breakTk) {
        assert Token.Type.BREAKTK.equals(breakTk.getType());
        this.breakTk = breakTk;
    }

    public Token getBreakTk() {
        return breakTk;
    }

    @Override
    public void output(PrintStream ps) {
        breakTk.output(ps);
    }

    @Override
    public int lineNumber() {
        return breakTk.lineNumber();
    }
}
