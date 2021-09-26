package frontend.exceptions.semantic;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

public class ModifyConstException extends FrontendException implements RequiredException {
    public ModifyConstException(int line, int column, String source) {
        super("Trying to modify the value of constant", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "h";
    }
}
