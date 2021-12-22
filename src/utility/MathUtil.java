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
