package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 函数调用, 函数定义和语句中缺少右小括号
 */
public class MissingRightParenthesisException extends FrontendException implements RequiredException {
    public MissingRightParenthesisException(int line, int column, String source) {
        super("Missing ')'", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "j";
    }
}
