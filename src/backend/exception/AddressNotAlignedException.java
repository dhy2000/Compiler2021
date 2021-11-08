package backend.exception;

/**
 * 读写内存的字或半字，地址未对齐
 */
public class AddressNotAlignedException extends Exception{
    private final int badAddress;
    private final String instruction;

    public AddressNotAlignedException(int badAddress, String instruction) {
        super("Address not aligned at " + String.format("%08x", badAddress) + " when executing " + instruction);
        this.badAddress = badAddress;
        this.instruction = instruction;
    }

    public int getBadAddress() {
        return badAddress;
    }

    public String getInstruction() {
        return instruction;
    }
}
