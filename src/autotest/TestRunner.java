package autotest;

import compiler.Config;
import compiler.MainCompiler;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 运行自动测试
 */
public class TestRunner {

    public static final boolean STOP_AT_FIRST_WRONG = true;             // stop auto-test at the first wrong-answer testcase
    public static final String EXPORT_TESTCASE_PATH = "testfile/tmp";  // path to export testcase, also the temporary path to run mips
    public static final String MARS_PATH = "testfile/Mars-Compile-2021.jar";
    public static final String MIPS_NAME = "mips.asm";

    private final PrintStream log = System.err; // can also be a file
    private final String rootPath = TestSet.ROOT_PATH;

    public enum Mode {
        PCODE, MIPS
    }

    private final Mode mode;

    private int count = 0; // correct testcases count

    public TestRunner(Mode mode) {
        this.mode = mode;
    }

    private String readFromInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    private void outputFromInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        int siz;
        while ((siz = in.read(buffer)) != -1) {
            out.write(buffer, 0, siz);
        }
        in.close();
        out.close();
    }

    private void exportPathCheck() throws IOException {
        File path = new File(EXPORT_TESTCASE_PATH);
        if (!path.exists()) {
            boolean r = path.mkdirs();
            if (!r) {
                throw new IOException("Failed to create " + path);
            }
        } else if (!path.isDirectory()) {
            boolean rDel = path.delete();
            if (!rDel) {
                throw new IOException("Failed to clean " + path);
            }
            boolean rMkdir = path.mkdirs();
            if (!rMkdir) {
                throw new IOException("Failed to create " + path);
            }
        }
    }

    private boolean runMips(String mips, FileInputStream input, String answer) throws Exception {
        exportPathCheck();
        String mipsPath = EXPORT_TESTCASE_PATH + File.separator + MIPS_NAME;
        FileOutputStream out = new FileOutputStream(mipsPath);
        out.write(mips.getBytes(StandardCharsets.UTF_8));
        out.close();
        Process mars = Runtime.getRuntime().exec("java -jar " + MARS_PATH + " nc " + mipsPath);
        OutputStream stdin = mars.getOutputStream(); // stdin of Mars
        InputStream stdout = mars.getInputStream(); // stdout of Mars
        InputStream stderr = mars.getErrorStream(); // stderr of Mars
        // send input-data into stdin
        outputFromInputStream(input, stdin);
        // get output-data from stdout
        final int status = mars.waitFor(); // wait MARS end
        String output = readFromInputStream(stdout);
        String error = readFromInputStream(stderr);
        if (status != 0) {
            throw new RuntimeException("Error running MARS: " + error);
        }
        return check(answer, output);
    }

    private boolean runTestCase(TestCase test) throws Exception {
        log.println("Running on testcase " + test.getName());
        FileInputStream testfile = new FileInputStream(rootPath + File.separator + test.getTestfile());
        FileInputStream stdin = new FileInputStream(rootPath + File.separator + test.getInput());
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        Config config = new Config();
        config.setSource(testfile);
        String answer = readFromInputStream(new FileInputStream(rootPath + File.separator + test.getOutput()));
        String output;
        MainCompiler compiler;
        switch (mode) {
            case PCODE:
                config.setInput(stdin);
                config.setTarget(Config.Operation.VM_RUNNER, new PrintStream(stdout));
                compiler = new MainCompiler(config);
                compiler.run();
                output = stdout.toString("UTF-8");
                stdout.close();
                // log.println("Pcode compiler done.");
                return check(answer, output);
            case MIPS:
                config.setTarget(Config.Operation.OBJECT_CODE, new PrintStream(stdout));
                compiler = new MainCompiler(config);
                compiler.run();
                output = stdout.toString("UTF-8");
                stdout.close();
                // log.println("Mips compiler done, starting MARS......");
                return runMips(output, stdin, answer);
            default:
                throw new AssertionError("Invalid test mode!");
        }
    }

    private void exportTestCase(TestCase test) {
        log.println("Exporting testcase " + test);
        try {
            exportPathCheck();
            FileInputStream testfile = new FileInputStream(rootPath + File.separator + test.getTestfile());
            FileInputStream stdin = new FileInputStream(rootPath + File.separator + test.getInput());
            FileInputStream answer = new FileInputStream(rootPath + File.separator + test.getOutput());
            String testfilePath = EXPORT_TESTCASE_PATH + File.separator + "testfile.txt";
            String stdinPath = EXPORT_TESTCASE_PATH + File.separator + "input.txt";
            String answerPath = EXPORT_TESTCASE_PATH + File.separator + "answer.txt";
            FileOutputStream storeTestfile = new FileOutputStream(testfilePath);
            FileOutputStream storeInputFile = new FileOutputStream(stdinPath);
            FileOutputStream storeAnswerFile = new FileOutputStream(answerPath);
            outputFromInputStream(testfile, storeTestfile);
            outputFromInputStream(stdin, storeInputFile);
            outputFromInputStream(answer, storeAnswerFile);
            log.println("Exported to " + testfilePath + ", " + stdinPath + ", " + answerPath);
        } catch (IOException e) {
            log.println("Failed to export testcase!");
            e.printStackTrace();
        }
    }

    public void runAll() {
        TestSet testSet = TestSet.getInstance();
        log.println(" --- Autotest Start (Running " + testSet.size() + " testcases) ---");
        for (TestCase test : testSet) {
            boolean status = false;
            long start = System.currentTimeMillis();
            try {
                status = runTestCase(test);
            } catch (Exception e) {
                log.println("Runtime error with Exception " + e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            } catch (AssertionError e) {
                log.println("Runtime error with AssertionError: " + e.getMessage());
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            if (status) {
                count++;
                log.println("Accepted, elapsed " + (end - start) + "ms.");
            } else {
                log.println("Wrong Answer, elapsed " + (end - start) + "ms.");
                if (STOP_AT_FIRST_WRONG) {
                    exportTestCase(test);
                    break;
                }
            }
        }
        log.println("Passed " + count + " of " + testSet.size() + " testcases.");
        log.println(" --- Autotest End --- ");
    }

    private boolean check(String answer, String output) {
        String[] linesAns = answer.split("\n", -1);
        String[] linesOut = output.split("\n", -1);

        int lengthAns = linesAns.length;
        int lengthOut = linesOut.length;

        // ignore empty lines at the end of file
        while (lengthAns > 0 && linesAns[lengthAns - 1].isEmpty()) { lengthAns--; }
        while (lengthOut > 0 && linesOut[lengthOut - 1].isEmpty()) { lengthOut--; }

        // compare each line
        int pos = 0;
        while (pos < lengthAns && pos < lengthOut) {
            if (!linesAns[pos].trim().equals(linesOut[pos].trim())) {
                log.println("We got '" + linesOut[pos] + "' when we expected '" + linesAns[pos] + "' at line " + pos + ".");
                return false;
            }
            pos++;
        }
        if (pos < lengthAns) {
            log.println("We got nothing when we expected '" + linesAns[pos] + "' at line " + pos + ".");
            return false;
        }
        if (pos < lengthOut) {
            log.println("We got '" + linesOut[pos] + "' when we expected nothing at line " + pos + ".");
            return false;
        }
        return true;
    }
}
