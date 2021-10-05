package frontend.syntax.expr.unary;

import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LVal implements BasePrimaryExp {

    private final Ident name;
    private final List<Index> indexes;

    public LVal(Ident name) {
        this.name = name;
        this.indexes = Collections.emptyList();
    }

    public LVal(Ident name, List<Index> indexes) {
        this.name = name;
        this.indexes = indexes;
    }

    public Ident getName() {
        return name;
    }

    public Iterator<Index> iterIndexes() {
        return indexes.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        name.output(ps);
        indexes.forEach(index -> index.output(ps));
        ps.println("<LVal>");
    }

    /**
     * 数组下标
     */
    public static class Index implements Component {
        private final Token leftBracket;
        private final Token rightBracket;
        private final Exp index;

        public Index(Token leftBracket, Token rightBracket, Exp index) {
            this.leftBracket = leftBracket;
            this.rightBracket = rightBracket;
            this.index = index;
        }

        public Token getLeftBracket() {
            return leftBracket;
        }

        public Token getRightBracket() {
            return rightBracket;
        }

        public Exp getIndex() {
            return index;
        }

        @Override
        public void output(PrintStream ps) {
            leftBracket.output(ps);
            index.output(ps);
            rightBracket.output(ps);
        }
    }
}
