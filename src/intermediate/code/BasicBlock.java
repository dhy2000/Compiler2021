package intermediate.code;

/**
 * 基本块, 连续执行的若干条中间指令。
 * 该节点是基本块的头部，有一个标签来标识。
 */
public class BasicBlock extends ILinkNode {
    private final String label;
    private final ILinkNode tail = new ILinkNode() {};

    public BasicBlock(String label) {
        this.label = label;
        // prev is null
    }

    public String getLabel() {
        return label;
    }

    public ILinkNode getHead() {
        return getNext();
    }

    public void setHead(ILinkNode node) {
        setNext(node);
        ILinkNode tail = node;
        while (tail.hasNext()) {
            tail = tail.getNext();
        }
        tail.setNext(this.tail);
        this.tail.setPrev(tail);
    }

    public ILinkNode getTail() {
        return tail.getPrev();
    }

    public void append(ILinkNode follow) {
        tail.setNext(follow);
        follow.setPrev(tail);
        ILinkNode tail = follow;
        while (tail.hasNext()) {
            tail = tail.getNext();
        }
        tail.setNext(this.tail);
        this.tail.setPrev(tail);
    }
}
