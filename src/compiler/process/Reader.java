package compiler.process;

import compiler.model.source.SourceBuffer;

import java.io.InputStream;
import java.util.Scanner;

public class Reader {

    private final InputStream input;

    public Reader(InputStream input) {
        this.input = input;
    }

    /**
     * 读取源文件 (读取完毕后会自动关闭输入流)
     * @return 源代码对象
     */
    public SourceBuffer read() {
        Scanner cin = new Scanner(input);
        SourceBuffer source = new SourceBuffer();
        while (cin.hasNext()) {
            String line = cin.nextLine();
            source.appendLine(line);
        }
        cin.close();
        return source;
    }
}
