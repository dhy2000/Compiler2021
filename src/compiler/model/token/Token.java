package compiler.model.token;

public abstract class Token {

    private final Type type;
    private final int line;

    public Token(Type type, int line) {
        this.type = type;
        this.line = line;
    }

    public String getTypeName() {
        return type.name();
    }

    public int getLineNumber() {
        return line;
    }

    public boolean isReserved() {
        return type.isReserved();
    }

    // non-reserve token must override this method
    public String getContent() {
        return type.getContent();
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
        IDENFR,
        INTCON,
        STRCON,
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
        AND("&&"),
        OR("||"),
        PLUS("+"),
        MINU("-"),
        MULT("*"),
        DIV("/"),
        MOD("%"),
        LEQ("<="),
        LSS("<"),
        GEQ(">="),
        GRE(">"),
        EQL("=="),
        NEQ("!="),
        NOT("!"),
        ASSIGN("="),
        SEMICN(";"),
        COMMA(","),
        LPARENT("("),
        RPARENT(")"),
        LBRACK("["),
        RBRACK("]"),
        LBRACE("{"),
        RBRACE("}")
        ;

        /**
         * 该种成分是否对应固定的内容(保留内容)
         */
        private final boolean reserved;
        /**
         * 若该成分为保留, 为其内容, 否则为空
         */
        private final String content;

        /**
         * 该保留成分是否同时满足标识符的语法
         */
        private final boolean identifier;

        Type() {
            this.reserved = false;
            this.content = "";
            this.identifier = false;
        }

        Type(String content) {
            this.reserved = true;
            this.content = content;
            this.identifier = false;
        }

        Type(String content, boolean identifier) {
            this.reserved = true;
            this.content = content;
            this.identifier = identifier;
        }

        public boolean isReserved() {
            return reserved;
        }

        public String getContent() {
            return content;
        }

        public boolean likeIdentifier() {
            return identifier;
        }
    }
}
