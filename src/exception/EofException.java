package exception;

public class EofException extends FrontendException {
    public EofException(int line, String syntax) {
        super("Unexpected EOF", line, "EOF in " + syntax);
    }
}
