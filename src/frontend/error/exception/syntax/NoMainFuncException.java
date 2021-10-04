package frontend.error.exception.syntax;

import frontend.error.exception.FrontendException;

public class NoMainFuncException extends FrontendException {
    public NoMainFuncException(int line, String source) {
        super("No main function", line, source);
    }
}
