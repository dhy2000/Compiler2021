package frontend.error.exception.syntax;

import frontend.error.ErrorTable;
import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

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

    private static void thrower(int line, String source) throws MissingSemicolonException {
        throw new MissingSemicolonException(line, source);
    }

    public static void registerError(int line, String source) {
        try {
            thrower(line, source);
        } catch (MissingSemicolonException e) {
            ErrorTable.getInstance().add(e);
        }
    }
}
