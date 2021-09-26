package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 标识符名字重定义
 */
public class DuplicatedIdentifierException extends FrontendException implements RequiredException {
    public DuplicatedIdentifierException(int line, int column, String source) {
        super("Duplicated identifier name", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "b";
    }
}
