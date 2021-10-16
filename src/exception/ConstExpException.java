package exception;

public class ConstExpException extends FrontendException {
    public ConstExpException(int line, String source) {
        super("ConstExp not const", line, source);
    }
}
