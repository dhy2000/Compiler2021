package autotest;

/**
 * 测试点
 */
public class TestCase {
    /*
        name: String
        testfile(path): String
        input(path): String
        output(path): String

        path: relative path
     */
    private final String name;
    private final String testfile;
    private final String input;
    private final String output;

    public TestCase(String name, String testfile, String input, String output) {
        this.name = name;
        this.testfile = testfile;
        this.input = input;
        this.output = output;
    }

    public String getName() {
        return name;
    }

    public String getTestfile() {
        return testfile;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return name + ": [" + testfile + ", " + input + ", " + output + "]";
    }
}
