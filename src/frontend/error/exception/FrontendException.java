package frontend.error.exception;

/**
 * 词法或语法错误
 */
public abstract class FrontendException extends Exception {

    private final int line; // 出错位置的行号
    private final String source;  // 出错位置相关的源代码

    public FrontendException(String tag, int line,String source) {
        super(String.format("%s at line %d: %s", tag, line, source));
        this.line = line;
        this.source = source;
    }

    public int getLineNumber() {
        return line;
    }

    public String getSource() {
        return source;
    }
}
