package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * printf 中格式字符与表达式个数不匹配
 */
public class PrintfMismatchException extends FrontendException implements RequiredException {
    public PrintfMismatchException(int line, int column, String source) {
        super("Mismatch between format string and params in printf", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "l";
    }
}
