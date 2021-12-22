package utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MathUtil {
    public static boolean isLog2(int num) {
        return (num & (num - 1)) == 0;
    }

    public static int log2(int num) {
        final int bitCount = (Integer.BYTES << 3) - 1;
        return bitCount - Integer.numberOfLeadingZeros(num);
    }

    public static int countLeadingZeros(int num) {
        int cnt = 0;
        for (int i = (Integer.BYTES << 3) - 1; i >= 0; i--) {
            if ((num & (1 << i)) != 0) {
                break;
            }
            cnt++;
        }
        return cnt;
    }

    public static int countLeadingOnes(int num) {
        return countLeadingZeros(~num);
    }

    public static int absoluteValue(int num) {
        return Math.abs(num);
    }

    public static int multiplyHigh(int num1, int num2) {
        long prod = (long) num1 * (long) num2;
        return (int) (prod >> (Integer.BYTES << 3));
    }

    public static long getUnsignedInt(int num) {
        return num & 0xFFFFFFFFL;
    }

    public static int[] divideU64To32(int hi, int lo, int divisor) {
        BigInteger n1 = BigInteger.valueOf(getUnsignedInt(hi)).shiftLeft(32);
        BigInteger n2 = BigInteger.valueOf(getUnsignedInt(lo));
        BigInteger n = n1.or(n2);
        BigInteger d = BigInteger.valueOf(divisor);
        BigInteger[] result = n.divideAndRemainder(d);
        return new int[]{result[0].intValueExact(), result[1].intValueExact()};
    }

    public static String encrypt(String message) {
        byte[] cipher;
        try {
            cipher = MessageDigest.getInstance("md5").digest(message.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("Encrypt not implemented!");
        }
        String ciphertext = new BigInteger(1, cipher).toString(16);
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < 32 - ciphertext.length(); i++) {
            prefix.append("0");
        }
        return prefix + ciphertext;
    }

}
