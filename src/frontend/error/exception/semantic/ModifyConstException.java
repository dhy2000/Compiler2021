package frontend.error.exception.semantic;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

public class ModifyConstException extends FrontendException implements RequiredException {
    public ModifyConstException(int line, String source) {
        super("Trying to modify the value of constant", line, source);
    }

    @Override
    public String getErrorTag() {
        return "h";
    }
}
