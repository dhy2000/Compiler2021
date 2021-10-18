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

    public void remove() {
        // prev.next = next
        if (hasPrev()) {
            getPrev().setNext(getNext());
        }
        // next.prev = prev
        if (hasNext()) {
            getNext().setPrev(getPrev());
        }
    }

    // usage: current.insertAfter(another)
    public void insertAfter(ILinkNode node) {
        node.setPrev(this);
        node.setNext(getNext());
        if (hasNext()) {
            getNext().setPrev(node);
        }
        setNext(node);
    }

    public void insertBefore(ILinkNode node) {
        node.setNext(this);
        node.setPrev(getPrev());
        if (hasPrev()) {
            getPrev().setNext(node);
        }
        setPrev(node);
    }
}
