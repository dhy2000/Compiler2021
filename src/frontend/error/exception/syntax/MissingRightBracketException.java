package frontend.error.exception.syntax;

import frontend.error.ErrorTable;
import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

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

    private static void thrower(int line, String source) throws MissingRightBracketException {
        throw new MissingRightBracketException(line, source);
    }

    public static void registerError(int line, String source) {
        try {
            thrower(line, source);
        } catch (MissingRightBracketException e) {
            ErrorTable.getInstance().add(e);
        }
    }
}
