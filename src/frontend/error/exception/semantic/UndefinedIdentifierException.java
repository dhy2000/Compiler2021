package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 标识符未定义
 */
public class UndefinedIdentifierException extends FrontendException implements RequiredException {
    public UndefinedIdentifierException(int line, String source) {
        super("Undefined Identifier", line, source);
    }

    @Override
    public String getErrorTag() {
        return "c";
    }
}
