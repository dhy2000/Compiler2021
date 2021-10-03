package frontend.syntax.expr.unary;

import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class FuncRParams implements Component {
    private final Exp first;
    private final List<Token> separators;   // commas
    private final List<Exp> params;

    public FuncRParams(Exp first, List<Token> separators, List<Exp> params) {
        assert separators.size() == params.size();
        this.first = first;
        this.separators = separators;
        this.params = params;
    }

    public Exp getFirst() {
        return first;
    }

    public Iterator<Token> iterSeparators() {
        return separators.listIterator();
    }

    public Iterator<Exp> iterParams() {
        return params.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        first.output(ps);
        Iterator<Token> iterSeparators = iterSeparators();
        Iterator<Exp> iterParams = iterParams();
        while (iterSeparators.hasNext() && iterParams.hasNext()) {
            Token separator = iterSeparators.next();
            Exp param = iterParams.next();
            separator.output(ps);
            param.output(ps);
        }
        ps.println("<FuncRParams>");
    }
}
