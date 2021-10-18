package intermediate.code.old;

/**
 * 中间代码的一种, 顺序执行的
 */
public interface InOrder {
    IntermediateCode getNext();

    void setNext(IntermediateCode code);
}
