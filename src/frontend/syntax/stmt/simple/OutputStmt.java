package frontend.syntax.stmt.simple;

import frontend.lexical.token.FormatString;
import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Exp;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class OutputStmt implements SplStmt {

    private final Token printfTk;
    private final Token leftParenthesis;
    private final Token rightParenthesis;
    private final FormatString formatString;
    private final List<Token> separators;
    private final List<Exp> parameters;

    public OutputStmt(Token printfTk,
                      Token leftParenthesis,
                      Token rightParenthesis,
                      FormatString formatString,
                      List<Token> separators,
                      List<Exp> parameters) {
        assert printfTk.getType().equals(Token.Type.PRINTFTK);
        assert leftParenthesis.getType().equals(Token.Type.LPARENT);
        assert Objects.isNull(rightParenthesis) || rightParenthesis.getType().equals(Token.Type.RPARENT);
        assert separators.size() == parameters.size();
        this.printfTk = printfTk;
        this.leftParenthesis = leftParenthesis;
        this.rightParenthesis = rightParenthesis;
        this.formatString = formatString;
        this.separators = separators;
        this.parameters = parameters;
    }

    public Token getPrintfTk() {
        return printfTk;
    }

    public Token getLeftParenthesis() {
        return leftParenthesis;
    }

    public Token getRightParenthesis() {
        return rightParenthesis;
    }

    public boolean hasRightParenthesis() {
        return Objects.nonNull(rightParenthesis);
    }

    public FormatString getFormatString() {
        return formatString;
    }

    public Iterator<Token> iterSeparators() {
        return separators.listIterator();
    }

    public Iterator<Exp> iterParameters() {
        return parameters.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        printfTk.output(ps);
        leftParenthesis.output(ps);
        formatString.output(ps);
        Iterator<Token> iterSeparators = iterSeparators();
        Iterator<Exp> iterParameters = iterParameters();
        while (iterSeparators.hasNext()) {
            Token separator = iterSeparators.next();
            Exp parameter = iterParameters.next();
            separator.output(ps);
            parameter.output(ps);
        }
        if (hasRightParenthesis()) {
            rightParenthesis.output(ps);
        }
    }

    @Override
    public int lineNumber() {
        return printfTk.lineNumber();
    }
}
