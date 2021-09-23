package com.compiler.exceptions;

/**
 * 未识别符号异常
 */
public class UnrecognizedTokenException extends SyntaxException {
    private final int line; // 出错位置的行号
    private final int column;   // 出错位置的列号
    private final String text;  // 出错位置往后的源代码文本(截取的长度由调用者决定)

    public UnrecognizedTokenException(int line, int column, String text) {
        super("Unrecognized Token at " + line + ":" + column + ": " + text);
        this.line = line;
        this.column = column;
        this.text = text;
    }

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return column;
    }

    public String getText() {
        return text;
    }
}
