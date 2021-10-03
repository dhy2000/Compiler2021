package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 有返回值的函数末尾缺少 return 语句
 */
public class MissingReturnException extends FrontendException implements RequiredException {
    public MissingReturnException(int line, String source) {
        super("Missing return stmt in function with return type", line, source);
    }

    @Override
    public String getErrorTag() {
        return "g";
    }
}
