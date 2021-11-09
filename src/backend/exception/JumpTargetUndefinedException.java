package backend.exception;

/**
 * 跳转到未加载指令的区域
 */
public class JumpTargetUndefinedException extends Exception {
    private final String badInstruction;

    public JumpTargetUndefinedException(String badInstruction) {
        super("Branch/Jump to undefined label: " + badInstruction);
        this.badInstruction = badInstruction;
    }

    public String getBadInstruction() {
        return badInstruction;
    }
}
