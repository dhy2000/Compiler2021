package backend.hardware;

import java.util.*;
import java.util.stream.Collectors;

public class Memory {
    private static final boolean ENABLE_TRACE = false;

    private final Map<Integer, String> stringConst = new HashMap<>();

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

    public int loadByte(int address) {
        int alignedAddress = address - (address & 0x3);
        int offset = address & 0x3;
        int word = memory.getOrDefault(alignedAddress, 0);
        int[] bytes = {word & 0xFF, (word >> 8) & 0xFF, (word >> 16) & 0xFF, (word >> 24) & 0xFF};
        return bytes[offset];
    }

    public String getString(int address) {
        return stringConst.get(address);
    }

    public void putString(int address, String string) {
        stringConst.put(address, string);
    }

    public List<Integer> storedStringAddresses() {
        return Collections.unmodifiableList(stringConst.keySet().stream().sorted(Integer::compare).collect(Collectors.toList()));
    }

    public List<Integer> modifiedAddresses() {
        return Collections.unmodifiableList(memory.keySet().stream().sorted(Integer::compare).collect(Collectors.toList()));
    }
}
