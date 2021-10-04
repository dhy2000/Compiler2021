package frontend.error.exception.syntax;

import frontend.error.ErrorTable;
import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 函数调用, 函数定义和语句中缺少右小括号
 */
public class MissingRightParenthesisException extends FrontendException implements RequiredException {
    public MissingRightParenthesisException(int line, String source) {
        super("Missing ')'", line, source);
    }

    @Override
    public String getErrorTag() {
        return "j";
    }

    private static void thrower(int line, String source) throws MissingRightParenthesisException {
        throw new MissingRightParenthesisException(line, source);
    }

    public static void registerError(int line, String source) {
        try {
            thrower(line, source);
        } catch (MissingRightParenthesisException e) {
            ErrorTable.getInstance().add(e);
        }
    }
}
