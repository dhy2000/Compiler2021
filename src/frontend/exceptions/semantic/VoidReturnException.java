package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * void 函数中出现带返回值的 return 语句
 */
public class VoidReturnException extends FrontendException implements RequiredException {
    public VoidReturnException(int line, String source) {
        super("Returning value in void function", line, source);
    }

    @Override
    public String getErrorTag() {
        return "f";
    }
}
