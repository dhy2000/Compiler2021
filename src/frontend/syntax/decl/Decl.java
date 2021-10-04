package frontend.syntax.decl;

import frontend.lexical.token.Token;
import frontend.syntax.stmt.complex.BlockItem;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Decl implements BlockItem, Constable {
    private final Token constTk; // ConstDecl or VarDecl
    private final Token bType;

    private final Def first;
    private final List<Token> separators;
    private final List<Def> follows;

    private final Token semicolon;

    public Decl(Token constTk, Token bType, Def first, List<Token> separators, List<Def> follows, Token semicolon) {
        assert (Objects.isNull(constTk)) || constTk.getType().equals(Token.Type.CONSTTK);
        assert bType.getType().equals(Token.Type.INTTK);
        assert separators.size() == follows.size();
        assert semicolon.getType().equals(Token.Type.SEMICN);
        this.constTk = constTk;
        this.bType = bType;
        this.first = first;
        this.separators = separators;
        this.follows = follows;
        this.semicolon = semicolon;
    }

    public Decl(Token bType, Def first, List<Token> separators, List<Def> follows, Token semicolon) {
        assert bType.getType().equals(Token.Type.INTTK);
        assert separators.size() == follows.size();
        assert semicolon.getType().equals(Token.Type.SEMICN);
        this.constTk = null;
        this.bType = bType;
        this.first = first;
        this.separators = separators;
        this.follows = follows;
        this.semicolon = semicolon;
    }


    public Token getBType() {
        return bType;
    }

    public Def getFirst() {
        return first;
    }

    public Iterator<Token> iterSeparators() {
        return separators.listIterator();
    }

    public Iterator<Def> iterFollows() {
        return follows.listIterator();
    }

    public Token getSemicolon() {
        return semicolon;
    }

    @Override
    public boolean isConst() {
        return Objects.nonNull(constTk);
    }

    @Override
    public void output(PrintStream ps) {
        // <ConstDecl> or <VarDecl>
        if (isConst()) {
            constTk.output(ps);
        }
        bType.output(ps);
        first.output(ps);
        Iterator<Token> iterSeparators = iterSeparators();
        Iterator<Def> iterFollows = iterFollows();
        while (iterSeparators.hasNext()) {
            Token separator = iterSeparators.next();
            Def followDef = iterFollows.next();
            separator.output(ps);
            followDef.output(ps);
        }
        semicolon.output(ps);
        if (isConst()) {
            ps.println("<ConstDecl>");
        } else {
            ps.println("<VarDecl>");
        }
    }
}
