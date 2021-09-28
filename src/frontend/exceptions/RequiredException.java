package frontend.exceptions;

/**
 * 课程设计中 "错误处理" 部分要求处理的错误
 */
public interface RequiredException {
    int getLineNumber();
    String getErrorTag();

    default String getOutput() {
        return getLineNumber() + " " + getErrorTag();
    }
}
