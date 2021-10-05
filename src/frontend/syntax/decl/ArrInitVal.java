package frontend.syntax.decl;

import frontend.lexical.token.Token;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ArrInitVal implements InitVal {
    private final boolean constant;

    private final Token leftBrace;
    private final Token rightBrace;
    private final InitVal first;
    private final List<Token> separators;
    private final List<InitVal> follows;

    // full
    public ArrInitVal(boolean constant, Token leftBrace, Token rightBrace, InitVal first, List<Token> separators, List<InitVal> follows) {
        assert leftBrace.getType().equals(Token.Type.LBRACE);
        assert rightBrace.getType().equals(Token.Type.RBRACE);
        assert separators.size() == follows.size();
        this.constant = constant;
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.first = first;
        this.separators = separators;
        this.follows = follows;
    }

    // single
    public ArrInitVal(boolean constant, Token leftBrace, Token rightBrace, InitVal first) {
        assert leftBrace.getType().equals(Token.Type.LBRACE);
        assert rightBrace.getType().equals(Token.Type.RBRACE);
        this.constant = constant;
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.first = first;
        this.separators = Collections.emptyList();
        this.follows = Collections.emptyList();
    }

    // empty
    public ArrInitVal(boolean constant, Token leftBrace, Token rightBrace) {
        assert leftBrace.getType().equals(Token.Type.LBRACE);
        assert rightBrace.getType().equals(Token.Type.RBRACE);
        this.constant = constant;
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.first = null;
        this.separators = Collections.emptyList();
        this.follows = Collections.emptyList();
    }

    public Token getLeftBrace() {
        return leftBrace;
    }

    public Token getRightBrace() {
        return rightBrace;
    }

    public boolean isEmpty() {
        return Objects.isNull(first);
    }

    public boolean isSingle() {
        return Objects.nonNull(first) && separators.isEmpty() && follows.isEmpty();
    }

    public boolean isMultiple() {
        return Objects.nonNull(first) && !separators.isEmpty() && !follows.isEmpty();
    }

    public InitVal getFirst() {
        return first;
    }

    public int size() {
        return follows.size();
    }

    public Iterator<Token> iterSeparators() {
        return separators.listIterator();
    }

    public Iterator<InitVal> iterFollows() {
        return follows.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        leftBrace.output(ps);
        if (isSingle()) {
            first.output(ps);
            if (isMultiple()) {
                Iterator<Token> iterSeparators = iterSeparators();
                Iterator<InitVal> iterFollows = iterFollows();
                while (iterSeparators.hasNext()) {
                    Token separator = iterSeparators.next();
                    InitVal initVal = iterFollows.next();
                    separator.output(ps);
                    initVal.output(ps);
                }
            }
        }
        rightBrace.output(ps);
        if (isConst()) {
            ps.println("<ConstInitVal>");
        } else {
            ps.println("<InitVal>");
        }
    }

    @Override
    public boolean isConst() {
        return constant;
    }
}
