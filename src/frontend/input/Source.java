package frontend.input;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Source {
    private final List<String> lines = new ArrayList<>();

    private int line = 0;
    private int column = 0;

    /**
     * 根据对源代码文件的输入流构造源代码存储类
     * @param input 输入流 (构造完成后自动关闭)
     */
    public Source(InputStream input) {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void appendLine(String line) {
        lines.add(line);
    }

    public int getLineCount() {
        return lines.size();
    }

    public int getLineIndex() { return line + 1; }

    public int getColumnIndex() { return column + 1; }

    public String getCurrentLine() {
        if (!reachedEndOfFile()) {
            return lines.get(line);
        } else { return ""; }
    }

    public String getRemainingLine() {
        return getCurrentLine().substring(column);
    }

    public void nextLine() {
        if (!reachedEndOfFile()) {
            line++;
            column = 0;
        }
    }

    public boolean reachedEndOfFile() {
        return line >= lines.size();
    }

    public boolean reachedEndOfLine() {
        return column >= getCurrentLine().length();
    }

    public void forward(int steps) {
        // if reached end of file, stop.
        int remainSteps = steps;
        while (!reachedEndOfFile() && remainSteps > 0) {
            int length = getCurrentLine().length();
            if (column + remainSteps > length) {
                line++;
                remainSteps -= (length - column + 1);
                column = 0;
            } else {
                column += remainSteps;
                remainSteps = 0;
            }
        }
    }

    public char currentChar() {
        if (reachedEndOfLine()) { return '\n'; }
        if (reachedEndOfFile()) { return 0; }
        return getCurrentLine().charAt(column);
    }

    public void skipBlanks() {
        while (!reachedEndOfFile() && Character.isWhitespace(currentChar())) {
            forward(1);
        }
    }

    public String followingSeq(int length) {
        if (reachedEndOfFile()) { return ""; }
        if (column + length >= getCurrentLine().length()) {
            return getCurrentLine().substring(column);
        } else {
            return getCurrentLine().substring(column, column + length);
        }
    }

    public String matchFollowing(Pattern pattern) {
        Matcher matcher = pattern.matcher(getRemainingLine());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    public void printAll(PrintStream ps) {
        lines.forEach(ps::println);
    }
}
