package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 缺少右中括号
 */
public class MissingRightBracketException extends FrontendException implements RequiredException {
    public MissingRightBracketException(int line, int column, String source) {
        super("Missing ']'", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "k";
    }
}
