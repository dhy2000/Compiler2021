package com.compiler.model.token;

/**
 * 词法成分种类表
 * 注意: 对于保留成分(保留字和运算符)有公共前缀的, 需贪心匹配 (例如 "==" 应放在 "=" 前面)
 */
public enum TokenType {
    IDENFR,
    INTCON,
    STRCON,
    MAINTK("main"),
    CONSTTK("const"),
    INTTK("int"),
    BREAKTK("break"),
    CONTINUETK("continue"),
    IFTK("if"),
    ELSETK("else"),
    AND("&&"),
    OR("||"),
    WHILETK("while"),
    GETINTTK("getint"),
    PRINTFTK("printf"),
    RETURNTK("return"),
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

    TokenType() {
        this.reserved = false;
        this.content = "";
    }

    TokenType(String content) {
        this.reserved = true;
        this.content = content;
    }

    public boolean isReserved() {
        return reserved;
    }

    public String getContent() {
        return content;
    }
}
