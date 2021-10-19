package intermediate.code;

/**
 * 基本块, 连续执行的若干条中间指令。
 * 该节点是基本块的头部，有一个标签来标识。
 */
public class BasicBlock extends ILinkNode {
    private final String label;
    private final ILinkNode tail = new ILinkNode() {};

    public enum Type {
        FUNC,   // 函数体
        BRANCH, // 分支
        LOOP,   // 循环
        BASIC   // 普通的 {}
    }

    private final Type type;

    public BasicBlock(String label, Type type) {
        this.label = label;
        this.type = type;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[BLOCK_" + type.name() + "]" + getLabel() + ":\n");
        ILinkNode node = getHead();
        while (node.hasNext()) {
            sb.append(node).append("\n");
            node = node.getNext();
        }
        return sb.toString();
    }
}
