package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;

import java.io.PrintStream;

public class ContinueStmt implements SplStmt {

    private final Token continueTk;

    public ContinueStmt(Token continueTk) {
        assert Token.Type.CONTINUETK.equals(continueTk.getType());
        this.continueTk = continueTk;
    }

    public Token getContinueTk() {
        return continueTk;
    }

    @Override
    public void output(PrintStream ps) {
        continueTk.output(ps);
    }
}
