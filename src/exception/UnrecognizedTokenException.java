package exception;

/**
 * 未识别符号异常
 */
public class UnrecognizedTokenException extends FrontendException {
    public UnrecognizedTokenException(int line, String source) {
        super("Unrecognized token", line, source);
    }
}
