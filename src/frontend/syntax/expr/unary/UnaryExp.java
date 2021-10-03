package frontend.syntax.expr.unary;

import frontend.lexical.token.Token;
import frontend.syntax.Component;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class UnaryExp implements Component {

    private final List<Token> unaryOps;
    private final BaseUnaryExp base;

    public UnaryExp(List<Token> unaryOps, BaseUnaryExp base) {
        this.unaryOps = unaryOps;
        this.base = base;
    }

    public Iterator<Token> iterUnaryOp() {
        return unaryOps.listIterator();
    }

    public BaseUnaryExp getBase() {
        return base;
    }

    @Override
    public void output(PrintStream ps) {
        int depth = 0;
        Iterator<Token> iterUnaryOp = iterUnaryOp();
        while (iterUnaryOp.hasNext()) {
            Token unaryOp = iterUnaryOp.next();
            unaryOp.output(ps);
            ps.println("<UnaryOp>");
            depth++;
        }
        base.output(ps); // output the base
        ps.println("<UnaryExp>");
        for (; depth > 0; depth--) {
            ps.println("<UnaryExp>");
        }
    }
}
