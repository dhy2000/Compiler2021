package frontend.exceptions;

/**
 * 词法或语法错误
 */
public abstract class FrontendException extends Exception {

    private final int line; // 出错位置的行号
    private final int column;   // 出错位置的列号
    private final String source;  // 出错位置往后的源代码文本(截取的长度由调用者决定)

    public FrontendException(String tag, int line, int column, String source) {
        super(String.format("%s at line %d col %d: %s", tag, line, column, source));
        this.line = line;
        this.column = column;
        this.source = source;
    }

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return column;
    }

    public String getSource() {
        return source;
    }
}
