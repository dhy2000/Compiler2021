package exception;

public class NoMainFuncException extends FrontendException {
    public NoMainFuncException(int line, String source) {
        super("No main function", line, source);
    }
}
