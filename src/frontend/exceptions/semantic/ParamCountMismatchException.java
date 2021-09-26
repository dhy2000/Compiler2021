package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 函数参数个数不匹配
 */
public class ParamCountMismatchException extends FrontendException implements RequiredException {
    public ParamCountMismatchException(int line, int column, String source) {
        super("Parameter count mismatch on function call", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "d";
    }
}
