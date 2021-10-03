package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 语句末尾缺失分号
 */
public class MissingSemicolonException extends FrontendException implements RequiredException {
    public MissingSemicolonException(int line, String source) {
        super("Missing ';'", line, source);
    }

    @Override
    public String getErrorTag() {
        return "i";
    }
}
