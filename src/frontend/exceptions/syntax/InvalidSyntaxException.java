package frontend.exceptions.syntax;

import frontend.exceptions.FrontendException;

public class InvalidSyntaxException extends FrontendException {
    public InvalidSyntaxException(int line, String syntaxName, String nextToken) {
        super("Invalid Syntax", line, nextToken + " at " + syntaxName);
    }
}
