package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;
import frontend.syntax.expr.unary.LVal;

import java.io.PrintStream;

/**
 * LVal++; / LVal--;
 */
public class UnaryStmt implements SplStmt {

    private final LVal lVal;
    private final Token unaryTk; // '++' or '--'

    public UnaryStmt(LVal lVal, Token unaryTk) {
        assert unaryTk.getType().equals(Token.Type.INC) || unaryTk.getType().equals(Token.Type.DEC);
        this.lVal = lVal;
        this.unaryTk = unaryTk;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Token getUnaryTk() {
        return unaryTk;
    }

    @Override
    public int lineNumber() {
        return lVal.getName().lineNumber();
    }

    @Override
    public void output(PrintStream ps) {
        lVal.output(ps);
        unaryTk.output(ps);
    }
}
