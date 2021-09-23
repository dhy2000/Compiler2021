package compiler.model.token;

/**
 * 词法成分种类表
 * 注意: 对于保留成分(保留字和运算符)有公共前缀的, 需贪心匹配 (例如 "==" 应放在 "=" 前面)
 */
public enum TokenType {
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
    AND("&&"),
    OR("||"),
    WHILETK("while", true),
    GETINTTK("getint", true),
    PRINTFTK("printf", true),
    RETURNTK("return", true),
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

    TokenType() {
        this.reserved = false;
        this.content = "";
        this.identifier = false;
    }

    TokenType(String content) {
        this.reserved = true;
        this.content = content;
        this.identifier = false;
    }

    TokenType(String content, boolean identifier) {
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
