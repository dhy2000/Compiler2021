package frontend.syntax.stmt.complex;

import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Cond;
import frontend.syntax.stmt.Stmt;

import java.io.PrintStream;
import java.util.Objects;

public class IfStmt implements CplStmt {

    private final Token ifTk;
    private final Token leftParenthesis;
    private final Cond condition;
    private final Token rightParenthesis;
    private final Stmt thenStmt;

    private final Token elseTk; // nullable
    private final Stmt elseStmt; // nullable

    public IfStmt(Token ifTk, Token leftParenthesis, Cond condition, Token rightParenthesis, Stmt thenStmt) {
        assert ifTk.getType().equals(Token.Type.IFTK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert Objects.isNull(rightParenthesis) || rightParenthesis.getType().equals(Token.Type.RPARENT);
        this.ifTk = ifTk;
        this.leftParenthesis = leftParenthesis;
        this.condition = condition;
        this.rightParenthesis = rightParenthesis;
        this.thenStmt = thenStmt;
        // no else statement
        this.elseTk = null;
        this.elseStmt = null;
    }

    public IfStmt(Token ifTk, Token leftParenthesis, Cond condition, Token rightParenthesis, Stmt thenStmt, Token elseTk, Stmt elseStmt) {
        assert ifTk.getType().equals(Token.Type.IFTK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert Objects.isNull(rightParenthesis) || rightParenthesis.getType().equals(Token.Type.RPARENT);
        assert Objects.nonNull(elseTk) && elseTk.getType().equals(Token.Type.ELSETK);
        assert Objects.nonNull(elseStmt);
        this.ifTk = ifTk;
        this.leftParenthesis = leftParenthesis;
        this.condition = condition;
        this.rightParenthesis = rightParenthesis;
        this.thenStmt = thenStmt;
        // with else statement
        this.elseTk = elseTk;
        this.elseStmt = elseStmt;
    }

    public Token getIfTk() {
        return ifTk;
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

    public Stmt getThenStmt() {
        return thenStmt;
    }

    public boolean hasElse() {
        return Objects.nonNull(elseTk) && Objects.nonNull(elseStmt);
    }

    public Token getElseTk() {
        return elseTk;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }

    @Override
    public void output(PrintStream ps) {
        ifTk.output(ps);
        leftParenthesis.output(ps);
        condition.output(ps);
        if (hasRightParenthesis()) {
            rightParenthesis.output(ps);
        }
        thenStmt.output(ps);
        if (hasElse()) {
            elseTk.output(ps);
            elseStmt.output(ps);
        }
    }
}
