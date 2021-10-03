package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

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
