package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;

public class InvalidSyntaxException extends FrontendException {
    public InvalidSyntaxException(int line, String source) {
        super("Invalid Syntax", line, source);
    }
}
