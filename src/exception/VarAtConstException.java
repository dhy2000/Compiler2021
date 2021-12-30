package exception;

public class VarAtConstException extends FrontendException {
    public VarAtConstException(int line, String source) {
        super("Variable at Constant Expression", line, source);
    }
}
