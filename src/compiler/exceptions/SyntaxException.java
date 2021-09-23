package compiler.exceptions;

/**
 * 词法或语法错误
 */
public abstract class SyntaxException extends Exception {

    private final int line; // 出错位置的行号
    private final int column;   // 出错位置的列号
    private final String text;  // 出错位置往后的源代码文本(截取的长度由调用者决定)

    public SyntaxException(String message, int line, int column, String text) {
        super(message);
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
