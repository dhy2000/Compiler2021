package frontend.lexical.token;

import frontend.syntax.Component;

import java.io.PrintStream;
import java.util.regex.Pattern;

public abstract class Token implements Component {

    private final Type refType;
    private final int line;
    private final String content;

    public Token(Type refType, int line, String content) {
        this.refType = refType;
        this.line = line;
        this.content = content;
    }

    public String typeName() {
        return refType.name();
    }

    public Type getType() {
        return refType;
    }

    public int lineNumber() {
        return line;
    }

    public String getContent() {
        return content;
    }

    public void output(PrintStream ps) {
        ps.println(typeName() + " " + getContent());
    }

    @Override
    public String toString() {
        return getContent();
    }

    /**
     * 词法成分种类表
     * 注意: 对于保留成分(保留字和运算符)有公共前缀的, 需贪心匹配 (例如 "==" 应放在 "=" 前面)
     */
    public enum Type {
        MAINTK("main", true),
        CONSTTK("const", true),
        INTTK("int", true),
        BREAKTK("break", true),
        CONTINUETK("continue", true),
        IFTK("if", true),
        ELSETK("else", true),
        VOIDTK("void", true),
        WHILETK("while", true),
        GETINTTK("getint", true),
        PRINTFTK("printf", true),
        RETURNTK("return", true),

        IDENFR("[_A-Za-z][_A-Za-z0-9]*"),
        INTCON("[0-9]+"),
        STRCON("\\\"[^\\\"]*\\\""),

        AND("&&"),
        OR("\\|\\|"),
        LEQ("<="),
        GEQ(">="),
        EQL("=="),
        NEQ("!="),

        PLUS("\\+"),
        MINU("-"),
        MULT("\\*"),
        DIV("/"),
        MOD("%"),
        LSS("<"),
        GRE(">"),
        NOT("!"),
        ASSIGN("="),
        SEMICN(";"),
        COMMA(","),
        LPARENT("\\("),
        RPARENT("\\)"),
        LBRACK("\\["),
        RBRACK("]"),
        LBRACE("\\{"),
        RBRACE("}")
        ;
        private final Pattern pattern;

        Type(String pattern) {
            this.pattern = Pattern.compile("^" + pattern);
        }

        Type(String pattern, boolean postAssert) {
            if (postAssert) {
                this.pattern = Pattern.compile("^" + pattern + "(?![_A-Za-z0-9])");
            } else {
                this.pattern = Pattern.compile("^" + pattern);
            }
        }

        public Pattern getPattern() {
            return pattern;
        }
    }

    public static Token newInstance(Type refType, int line, String content) {
        switch (refType) {
            case IDENFR: return new Ident(content, line);
            case INTCON: return new IntConst(content, line);
            case STRCON: return new FormatString(content, line);
            default: return new ReservedToken(refType, line, content);
        }
    }
}
