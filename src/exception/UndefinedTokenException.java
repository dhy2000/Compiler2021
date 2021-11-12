package exception;

/**
 * 未识别符号异常
 */
public class UndefinedTokenException extends FrontendException {
    public UndefinedTokenException(int line, String source) {
        super("Unrecognized token", line, source);
    }
}
