package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 函数参数类型不匹配
 */
public class ParamTypeMismatchException extends FrontendException implements RequiredException {
    public ParamTypeMismatchException(int line, int column, String source) {
        super("Parameter type mismatch on function call", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "e";
    }
}
