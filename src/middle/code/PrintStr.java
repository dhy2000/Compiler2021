package middle.code;

/**
 * 输出字符串，对应 Mars 的 4 号 syscall
 * 参数是代表这个字符串首地址的标签
 */
public class PrintStr extends ILinkNode {
    private final String label;

    public PrintStr(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "PRINT_STR " + label;
    }
}
