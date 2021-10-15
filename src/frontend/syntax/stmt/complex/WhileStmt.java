package frontend.syntax.stmt.complex;

import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Cond;
import frontend.syntax.stmt.Stmt;

import java.io.PrintStream;
import java.util.Objects;

public class WhileStmt implements CplStmt {

    private final Token whileTk;
    private final Token leftParenthesis;
    private final Cond condition;
    private final Token rightParenthesis;
    private final Stmt stmt;

    public WhileStmt(Token whileTk, Token leftParenthesis, Cond condition, Token rightParenthesis, Stmt stmt) {
        assert whileTk.getType().equals(Token.Type.WHILETK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert Objects.isNull(rightParenthesis) || rightParenthesis.getType().equals(Token.Type.RPARENT);
        this.whileTk = whileTk;
        this.leftParenthesis = leftParenthesis;
        this.condition = condition;
        this.rightParenthesis = rightParenthesis;
        this.stmt = stmt;
    }

    public Token getWhileTk() {
        return whileTk;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Cond getCondition() {
        return condition;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public boolean hasRightParenthesis() {
        return Objects.nonNull(rightParenthesis);
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public void output(PrintStream ps) {
        whileTk.output(ps);
        leftParenthesis.output(ps);
        condition.output(ps);
        if (hasRightParenthesis()) {
            rightParenthesis.output(ps);
        }
        stmt.output(ps);
    }
}
