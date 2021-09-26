package frontend.exceptions;

/**
 * 未识别符号异常
 */
public class UnrecognizedTokenException extends FrontendException {
    public UnrecognizedTokenException(int line, int column, String text) {
        super("Unrecognized token", line, column, text);
    }
}
