package frontend.syntax.func;

import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.stmt.complex.Block;

import java.io.PrintStream;
import java.util.Objects;

public class FuncDef implements Component {
    private final FuncType type;
    private final Ident name;
    private final Token leftParenthesis;
    private final Token rightParenthesis;
    private final FuncFParams fParams; // nullable
    private final Block body;

    // with fParams
    public FuncDef(Token funcType, Ident name, Token leftParenthesis, Token rightParenthesis, FuncFParams fParams, Block body) {
        this.type = new FuncType(funcType);
        this.name = name;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.fParams = fParams;
        this.body = body;
    }

    // without fParams
    public FuncDef(Token funcType, Ident name, Token leftParenthesis, Token rightParenthesis, Block body) {
        this.type = new FuncType(funcType);
        this.name = name;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.fParams = null;
        this.body = body;
    }

    public FuncType getType() {
        return type;
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

    public boolean hasFParams() {
        return Objects.nonNull(fParams);
    }

    public FuncFParams getFParams() {
        return fParams;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public void output(PrintStream ps) {
        type.output(ps);
        name.output(ps);
        leftParenthesis.output(ps);
        if (Objects.nonNull(fParams)) {
            fParams.output(ps);
        }
        rightParenthesis.output(ps);
        body.output(ps);
        ps.println("<FuncDef>");
    }

    public static class FuncType implements Component {
        private final Token type;

        FuncType(Token type) {
            assert type.getType().equals(Token.Type.INTTK) || type.getType().equals(Token.Type.VOIDTK);
            this.type = type;
        }

        public Token getType() {
            return type;
        }

        @Override
        public void output(PrintStream ps) {
            type.output(ps);
            ps.println("<FuncType>");
        }
    }
}
