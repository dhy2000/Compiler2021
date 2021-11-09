package backend;

import backend.hardware.Memory;
import backend.hardware.RegisterFile;
import backend.instruction.MipsInstruction;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储所有目标代码的容器
 */
public class Mips {
    public static final int STRING_START_ADDRESS = 0x10000000; // .data 0x10000000 --> string constant
    public static final int DATA_START_ADDRESS = RegisterFile.GLOBAL_POINTER_INIT; // .data 0x10008000 --> $gp
    public static final int DATA_LIMIT_ADDRESS = 0x10040000; // heap base address

    // 字符串常量, 单独管理标签名以及内容
    private final Map<String, String> stringConstant = new HashMap<>();
    private final Map<String, Integer> stringConstantAddress = new HashMap<>();

    // 标签对应的指令
    private final Map<String, MipsInstruction> labelToInstruction = new HashMap<>();

    // 初始内存布局（在 .data 中用伪指令填入）
    private final Memory initMem = new Memory();

    private final MipsInstruction entry = new MipsInstruction() {
        @Override
        public String instrToString() {
            return "nop";
        }

        @Override
        public void execute(RegisterFile rf, Memory mem) {

        }

        @Override
        public boolean isJump(RegisterFile rf) {
            return false;
        }
    };
    private final MipsInstruction tail = new MipsInstruction() {
        @Override
        public String instrToString() {
            return "nop";
        }

        @Override
        public void execute(RegisterFile rf, Memory mem) {

        }

        @Override
        public boolean isJump(RegisterFile rf) {
            return false;
        }
    };

    public Mips() {
        tail.setPrev(entry);
        entry.setNext(tail);
    }

    public MipsInstruction getFirstInstruction() {
        return (MipsInstruction) entry.getNext();
    }

    public void append(MipsInstruction follow) {
        MipsInstruction last = (MipsInstruction) tail.getPrev();
        last.setNext(follow);
        follow.setPrev(last);
        MipsInstruction tail = follow;
        while (tail.hasNext()) {
            tail = (MipsInstruction) tail.getNext();
        }
        tail.setNext(this.tail);
        this.tail.setPrev(tail);
    }
}
