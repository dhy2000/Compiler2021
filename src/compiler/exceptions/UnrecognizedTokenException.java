package compiler.exceptions;

/**
 * 未识别符号异常
 */
public class UnrecognizedTokenException extends SyntaxException {

    public UnrecognizedTokenException(int line, int column, String text) {
        super("Unrecognized Token: " + text, line, column, text);
    }

}
