package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 在非循环块中使用 break 和 continue
 */
public class ControlFlowException extends FrontendException implements RequiredException {
    public ControlFlowException(int line, String source) {
        super("Break or Continue outside a loop block", line, source);
    }

    @Override
    public String getErrorTag() {
        return "m";
    }
}
