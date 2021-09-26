package frontend.source;

import java.io.InputStream;
import java.util.Scanner;

public class SourceReader {

    private final InputStream input;

    public SourceReader(InputStream input) {
        this.input = input;
    }

    /**
     * 读取源文件 (读取完毕后会自动关闭输入流)
     * @return 源代码对象
     */
    public Source read() {
        Scanner cin = new Scanner(input);
        Source source = new Source();
        while (cin.hasNext()) {
            String line = cin.nextLine();
            source.appendLine(line);
        }
        cin.close();
        return source;
    }
}
