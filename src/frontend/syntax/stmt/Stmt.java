package frontend.syntax.stmt;

import frontend.lexical.token.Token;
import frontend.syntax.stmt.complex.BlockItem;
import frontend.syntax.stmt.complex.CplStmt;
import frontend.syntax.stmt.simple.SplStmt;

import java.io.PrintStream;
import java.util.Objects;

public class Stmt implements BlockItem {

    private final SplStmt simpleStmt;
    private final CplStmt complexStmt;
    private final Token semicolon;

    private final Type type;

    // Empty Statement
    public Stmt(Token semicolon) {
        assert Objects.isNull(semicolon) || semicolon.getType().equals(Token.Type.SEMICN);
        this.simpleStmt = null;
        this.complexStmt = null;
        this.semicolon = semicolon;
        this.type = Type.EMPTY;
    }

    // Simple Statement with ';'
    public Stmt(SplStmt simpleStmt, Token semicolon) {
        assert Objects.isNull(semicolon) || semicolon.getType().equals(Token.Type.SEMICN);
        this.simpleStmt = simpleStmt;
        this.semicolon = semicolon;
        this.complexStmt = null;
        this.type = Type.SIMPLE;
    }

    // Complex Statement with no ';' ending with
    public Stmt(CplStmt complexStmt) {
        this.simpleStmt = null;
        this.semicolon = null;
        this.complexStmt = complexStmt;
        this.type = Type.COMPLEX;
    }

    public boolean isEmpty() {
        return type == Type.EMPTY;
    }

    public boolean isSimple() {
        return type == Type.SIMPLE;
    }

    public boolean isComplex() {
        return type == Type.COMPLEX;
    }

    public SplStmt getSimpleStmt() {
        return simpleStmt;
    }

    public CplStmt getComplexStmt() {
        return complexStmt;
    }

    public Token getSemicolon() {
        return semicolon;
    }

    public boolean hasSemicolon() {
        return Objects.nonNull(semicolon);
    }

    @Override
    public void output(PrintStream ps) {
        if (isEmpty()) {
            if (hasSemicolon()) {
                semicolon.output(ps);
            }
        } else if (isSimple()) {
            simpleStmt.output(ps);
            if (hasSemicolon()) {
                semicolon.output(ps);
            }
        } else {
            complexStmt.output(ps);
        }
        ps.println("<Stmt>");
    }

    public enum Type {
        EMPTY, SIMPLE, COMPLEX
    }
}
