package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 函数参数类型不匹配
 */
public class ParamTypeMismatchException extends FrontendException implements RequiredException {
    public ParamTypeMismatchException(int line, String source) {
        super("Parameter type mismatch on function call", line, source);
    }

    @Override
    public String getErrorTag() {
        return "e";
    }
}
