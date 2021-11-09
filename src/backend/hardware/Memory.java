package backend.hardware;

import java.util.Map;
import java.util.TreeMap;

public class Memory {
    private static final boolean ENABLE_TRACE = true;

    private final Map<Integer, Integer> memory = new TreeMap<>(); // <Address-by-word, Word>, not store in this map if not modified

    public Memory() {

    }

    // address 均以字节为单位，与 MIPS 指令相同
    public int loadWord(int address) {
        int alignedAddress = address - (address & 0x3);
        return memory.getOrDefault(alignedAddress, 0);
    }

    public void storeWord(int address, int value) {
        int alignedAddress = address - (address & 0x3);
        if (ENABLE_TRACE) {
            System.err.printf("*%08x <= %08x", alignedAddress, value);
        }
        if (value != 0) {
            memory.put(alignedAddress, value);
        } else {
            memory.remove(alignedAddress);
        }
    }
}
