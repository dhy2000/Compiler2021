package backend.instruction;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;
import intermediate.code.ILinkNode;

/**
 * MIPS 指令的基本类型
 */
public abstract class MipsInstruction extends ILinkNode {

    public MipsInstruction() {}

    private String label = "";  // 指令自己的标签(不是跳转指令的目标!)
    private String comment = "";

    public boolean hasLabel() {
        return !label.isEmpty();
    }

    public boolean hasComment() {
        return !comment.isEmpty();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public abstract String instrToString();

    public abstract void execute(RegisterFile rf, Memory mem); // 指令执行对寄存器和内存的行为

    public abstract boolean isJump(RegisterFile rf); // 执行当前指令后是否跳转到 Label

    public String getJumpTarget() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasLabel()) {
            sb.append(getLabel()).append(": ");
        }
        sb.append(instrToString());
        if (hasComment()) {
            sb.append("  # ").append(getComment());
        }
        return sb.toString();
    }

    public static MipsInstruction nop() {
        return new MipsInstruction() {
            @Override
            public String instrToString() {
                return "";
            }

            @Override
            public void execute(RegisterFile rf, Memory mem) {

            }

            @Override
            public boolean isJump(RegisterFile rf) {
                return false;
            }
        };
    }
}
