package intermediate.code;

/**
 * 含有两种去向的
 */
public interface Branch {
    IntermediateCode getThen();
    IntermediateCode getElse();
}
