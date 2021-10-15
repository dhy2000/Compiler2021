package frontend.syntax.stmt.simple;

import frontend.lexical.token.Token;
import frontend.syntax.expr.unary.LVal;

import java.io.PrintStream;
import java.util.Objects;

public class InputStmt implements SplStmt {

    private final LVal leftVal;
    private final Token assignTk;
    private final Token getIntTk;
    private final Token leftParenthesis;
    private final Token rightParenthesis;

    public InputStmt(LVal leftVal, Token assignTk, Token getIntTk, Token leftParenthesis, Token rightParenthesis) {
        assert assignTk.getType().equals(Token.Type.ASSIGN);
        assert getIntTk.getType().equals(Token.Type.GETINTTK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert Objects.isNull(rightParenthesis) || rightParenthesis.getType().equals(Token.Type.RPARENT);
        this.leftVal = leftVal;
        this.assignTk = assignTk;
        this.getIntTk = getIntTk;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
    }

    public LVal getLeftVal() {
        return leftVal;
    }

    public Token getAssignTk() {
        return assignTk;
    }

    public Token getGetIntTk() {
        return getIntTk;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public boolean hasRightParenthesis() {
        return Objects.nonNull(rightParenthesis);
    }

    @Override
    public void output(PrintStream ps) {
        leftVal.output(ps);
        assignTk.output(ps);
        getIntTk.output(ps);
        leftParenthesis.output(ps);
        if (hasRightParenthesis()) {
            rightParenthesis.output(ps);
        }
    }
}
