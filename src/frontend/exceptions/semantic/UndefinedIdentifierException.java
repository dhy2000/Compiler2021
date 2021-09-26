package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 标识符未定义
 */
public class UndefinedIdentifierException extends FrontendException implements RequiredException {
    public UndefinedIdentifierException(int line, int column, String source) {
        super("Undefined Identifier", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "c";
    }
}
