package intermediate.block;

import intermediate.code.old.InOrder;
import intermediate.code.old.IntermediateCode;

/**
 * 连续执行的一块代码, 维护头部和尾部
 */
public class BasicBlock {
    private final IntermediateCode head;
    private IntermediateCode tail;

    public BasicBlock(IntermediateCode head) {
        this.head = head;
        this.tail = head;
    }

    BasicBlock(IntermediateCode head, IntermediateCode tail) {
        this.head = head;
        this.tail = tail;
    }

    public IntermediateCode getHead() {
        return head;
    }

    public IntermediateCode getTail() {
        return tail;
    }

    public void append(IntermediateCode next) {
        if (tail instanceof InOrder) {
            ((InOrder) tail).setNext(next);
            this.tail = next;
        } else {
            throw new AssertionError("Append at tail of basic block!");
        }
    }

    public BasicBlock split(InOrder i) {
        BasicBlock ret = new BasicBlock(i.getNext(), this.tail);
        this.tail = (IntermediateCode) i;
        return ret;
    }
}
