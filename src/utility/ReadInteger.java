package utility;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadInteger {
    public static int readInt(BufferedReader input) {
        try {
            int c = input.read();
            int value = 0;
            boolean negative = false;
            while (c != -1 && !(c >= 48 && c <= 57)) {
                if (c == 45) {  // '-'
                    negative = true;
                }
                c = input.read();
            }
            if (c == -1) {
                return -1;
            }
            while ((c >= 48 && c <= 57)) {
                value = value * 10 + (c - 48);
                c = input.read();
            }
            return negative ? -value : value;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
