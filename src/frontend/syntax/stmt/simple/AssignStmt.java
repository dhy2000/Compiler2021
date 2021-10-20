package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.unary.LVal;

import java.io.PrintStream;

public class AssignStmt implements SplStmt {

    private final LVal leftVal;
    private final Token assignTk;
    private final Exp exp;

    public AssignStmt(LVal leftVal, Token assignTk, Exp exp) {
        this.leftVal = leftVal;
        this.assignTk = assignTk;
        this.exp = exp;
    }

    public LVal getLeftVal() {
        return leftVal;
    }

    public Token getAssignTk() {
        return assignTk;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void output(PrintStream ps) {
        leftVal.output(ps);
        assignTk.output(ps);
        exp.output(ps);
    }

    @Override
    public int lineNumber() {
        return leftVal.getName().lineNumber();
    }
}
