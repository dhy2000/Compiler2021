package compiler.exceptions;

public class StringNotClosedException extends SyntaxException {
    public StringNotClosedException(int line, int column, String text) {
        super("String is not closed." + text, line, column, text);
    }
}
