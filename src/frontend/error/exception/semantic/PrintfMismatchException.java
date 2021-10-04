package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * printf 中格式字符与表达式个数不匹配
 */
public class PrintfMismatchException extends FrontendException implements RequiredException {
    public PrintfMismatchException(int line, String source) {
        super("Mismatch between format string and params in printf", line, source);
    }

    @Override
    public String getErrorTag() {
        return "l";
    }
}
