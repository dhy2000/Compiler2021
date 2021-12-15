package utility;

public class MathUtil {
    public static boolean isLog2(int num) {
        return (num & (num - 1)) == 0;
    }

    public static int log2(int num) {
        final int bitCount = (Integer.BYTES << 3) - 1;
        return bitCount - Integer.numberOfLeadingZeros(num);
    }
}
