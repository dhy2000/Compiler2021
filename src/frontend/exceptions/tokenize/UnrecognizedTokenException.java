package frontend.exceptions.tokenize;

import frontend.exceptions.FrontendException;

/**
 * 未识别符号异常
 */
public class UnrecognizedTokenException extends FrontendException {
    public UnrecognizedTokenException(int line, int column, String source) {
        super("Unrecognized token", line, column, source);
    }
}
