package frontend.syntax.stmt.complex;

import frontend.lexical.token.Token;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class Block implements CplStmt {

    private final Token leftBrace;
    private final Token rightBrace;
    private final List<BlockItem> items;

    public Block(Token leftBrace, Token rightBrace, List<BlockItem> items) {
        assert leftBrace.getType().equals(Token.Type.LBRACE);
        assert rightBrace.getType().equals(Token.Type.RBRACE);
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.items = items;
    }

    public Token getLeftBrace() {
        return leftBrace;
    }

    public Token getRightBrace() {
        return rightBrace;
    }

    public int size() {
        return items.size();
    }

    public Iterator<BlockItem> iterItems() {
        return items.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        leftBrace.output(ps);
        for (BlockItem item : items) {
            item.output(ps);
        }
        rightBrace.output(ps);
        ps.println("<Block>");
    }
}
