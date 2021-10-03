package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 缺少右中括号
 */
public class MissingRightBracketException extends FrontendException implements RequiredException {
    public MissingRightBracketException(int line, String source) {
        super("Missing ']'", line, source);
    }

    @Override
    public String getErrorTag() {
        return "k";
    }
}
