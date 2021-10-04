package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 标识符名字重定义
 */
public class DuplicatedIdentifierException extends FrontendException implements RequiredException {
    public DuplicatedIdentifierException(int line, String source) {
        super("Duplicated identifier name", line, source);
    }

    @Override
    public String getErrorTag() {
        return "b";
    }
}
