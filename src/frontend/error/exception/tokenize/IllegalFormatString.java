package frontend.error.exception.tokenize;

import frontend.error.exception.FrontendException;
import frontend.error.exception.RequiredException;

/**
 * 格式字符串中含有非法符号
 */
public class IllegalFormatString extends FrontendException implements RequiredException {

    public IllegalFormatString(int line, String source) {
        super("Illegal character in Format String", line, source);
    }

    @Override
    public String getErrorTag() {
        return "a";
    }
}
