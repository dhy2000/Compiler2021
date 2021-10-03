package frontend.syntax.expr.unary;

import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;

import java.io.PrintStream;
import java.util.Objects;

public class FunctionCall implements BaseUnaryExp {

    private final Ident name;   // ident
    private final Token leftParenthesis; // '('
    private final Token rightParenthesis; // ')'
    private final FuncRParams params;

    public FunctionCall(Ident name, Token leftParenthesis, Token rightParenthesis, FuncRParams params) {
        this.name = name;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.params = params;
    }

    public FunctionCall(Ident name, Token leftParenthesis, Token rightParenthesis) {
        this.name = name;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.params = null;
    }

    public Ident getName() {
        return name;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public boolean hasParams() {
        return Objects.nonNull(params);
    }

    public FuncRParams getParams() {
        return params;
    }

    @Override
    public void output(PrintStream ps) {
        name.output(ps);
        leftParenthesis.output(ps);
        if (Objects.nonNull(params)) {
            params.output(ps);
        }
        rightParenthesis.output(ps);
    }
}
