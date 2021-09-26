package frontend.exceptions.tokenize;

import frontend.exceptions.FrontendException;
import frontend.exceptions.RequiredException;

/**
 * 格式字符串中含有非法符号
 */
public class IllegalFormatString extends FrontendException implements RequiredException {

    public IllegalFormatString(int line, int column, String source) {
        super("Illegal character in Format String", line, column, source);
    }

    @Override
    public String getErrorTag() {
        return "a";
    }
}
