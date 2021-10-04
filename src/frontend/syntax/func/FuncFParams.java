package frontend.syntax.func;

import frontend.lexical.token.Token;
import frontend.syntax.Component;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FuncFParams implements Component {

    private final FuncFParam first;
    private final List<Token> separators;
    private final List<FuncFParam> follows;

    public FuncFParams(FuncFParam first, List<Token> separators, List<FuncFParam> follows) {
        assert separators.size() == follows.size();
        this.first = first;
        this.separators = separators;
        this.follows = follows;
    }

    public FuncFParams(FuncFParam first) {
        this.first = first;
        this.separators = Collections.emptyList();
        this.follows = Collections.emptyList();
    }

    public FuncFParam getFirst() {
        return first;
    }

    public Iterator<Token> iterSeparators() {
        return separators.listIterator();
    }

    public Iterator<FuncFParam> iterFollows() {
        return follows.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        first.output(ps);
        Iterator<Token> iterSeparators = iterSeparators();
        Iterator<FuncFParam> iterFollows = iterFollows();
        while (iterSeparators.hasNext()) {
            Token separator = iterSeparators.next();
            FuncFParam followFParam = iterFollows.next();
            separator.output(ps);
            followFParam.output(ps);
        }
        ps.println("<FuncFParams>");
    }
}
