package exception;

public class UnexpectedEofException extends FrontendException {
    public UnexpectedEofException(int line, String syntax) {
        super("Unexpected EOF", line, "EOF in " + syntax);
    }
}
