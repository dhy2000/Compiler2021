package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 函数参数个数不匹配
 */
public class ParamCountMismatchException extends FrontendException implements RequiredException {
    public ParamCountMismatchException(int line, String source) {
        super("Parameter count mismatch on function call", line, source);
    }

    @Override
    public String getErrorTag() {
        return "d";
    }
}
