package compiler.exceptions;

public class IntegerException extends SyntaxException {
    public IntegerException(int line, int column, String text) {
        super("IntegerException \"" + text + "\"", line, column, text);
    }
}
