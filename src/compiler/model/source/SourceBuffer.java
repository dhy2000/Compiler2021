package compiler.model.source;

import java.util.ArrayList;
import java.util.List;

public class SourceBuffer {
    private final List<String> lines = new ArrayList<>();

    private int lineIndex = 0;
    private int columnIndex = 0;

    public SourceBuffer() {

    }

    public void appendLine(String line) {
        lines.add(line);
    }

    public int getLineCount() {
        return lines.size();
    }

    public int getLineIndex() { return lineIndex + 1; }

    public int getColumnIndex() { return columnIndex + 1; }

    public String getCurrentLine() {
        if (!reachedEndOfFile()) {
            return lines.get(lineIndex);
        } else { return ""; }
    }

    public String getRemainingLine() {
        return getCurrentLine().substring(columnIndex);
    }

    public void toNextLine() {
        if (!reachedEndOfFile()) {
            lineIndex++;
            columnIndex = 0;
        }
    }

    public boolean reachedEndOfFile() {
        return lineIndex >= lines.size();
    }

    public boolean reachedEndOfLine() {
        return columnIndex >= getCurrentLine().length();
    }

    public void forward(int steps) {
        // if reached end of file, stop.
        int remainSteps = steps;
        while (!reachedEndOfFile() && remainSteps > 0) {
            int length = getCurrentLine().length();
            if (columnIndex + remainSteps > length) {
                lineIndex++;
                remainSteps -= (length - columnIndex + 1);
                columnIndex = 0;
            } else {
                columnIndex += remainSteps;
                remainSteps = 0;
            }
        }
    }

    public char currentChar() {
        if (reachedEndOfLine()) { return '\n'; }
        if (reachedEndOfFile()) { return 0; }
        return getCurrentLine().charAt(columnIndex);
    }

    public void skipBlanks() {
        while (!reachedEndOfFile() && Character.isWhitespace(currentChar())) {
            forward(1);
        }
    }

    public String followingSeq(int length) {
        if (reachedEndOfFile()) { return ""; }
        if (columnIndex + length >= getCurrentLine().length()) {
            return getCurrentLine().substring(columnIndex);
        } else {
            return getCurrentLine().substring(columnIndex, columnIndex + length);
        }
    }
}
