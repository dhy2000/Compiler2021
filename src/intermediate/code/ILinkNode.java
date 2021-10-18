package intermediate.code;

import java.util.Objects;

/**
 * 中间代码(链表形式)的一个节点
 */
public abstract class ILinkNode {
    private ILinkNode prev;
    private ILinkNode next;

    public ILinkNode() {}

    public void setPrev(ILinkNode prev) {
        this.prev = prev;
    }

    public void setNext(ILinkNode next) {
        this.next = next;
    }

    public ILinkNode getPrev() {
        return prev;
    }

    public ILinkNode getNext() {
        return next;
    }

    public boolean hasPrev() {
        return Objects.nonNull(prev);
    }

    public boolean hasNext() {
        return Objects.nonNull(next);
    }

    public static void remove(ILinkNode node) {
        // TODO
    }

    public static void insertAfter(ILinkNode node) {
        // TODO
    }

    public static void insertBefore(ILinkNode node) {
        // TODO
    }
}
