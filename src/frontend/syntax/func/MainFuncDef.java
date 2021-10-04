package frontend.syntax.func;

import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.stmt.complex.Block;

import java.io.PrintStream;

public class MainFuncDef implements Component {
    private final Token intTk;
    private final Token mainTk;
    private final Token leftParenthesis;
    private final Token rightParenthesis;
    private final Block body;

    public MainFuncDef(Token intTk, Token mainTk, Token leftParenthesis, Token rightParenthesis, Block body) {
        assert intTk.getType().equals(Token.Type.INTTK);
        assert mainTk.getType().equals(Token.Type.MAINTK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert rightParenthesis.getType().equals(Token.Type.RPARENT);
        this.intTk = intTk;
        this.mainTk = mainTk;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.body = body;
    }

    public Token getIntTk() {
        return intTk;
    }

    public Token getMainTk() {
        return mainTk;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public Block getBody() {
        return body;
    }

    @Override
    public void output(PrintStream ps) {
        intTk.output(ps);
        mainTk.output(ps);
        leftParenthesis.output(ps);
        rightParenthesis.output(ps);
        body.output(ps);
    }
}
