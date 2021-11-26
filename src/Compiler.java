import autotest.TestRunner;
import compiler.Config;
import compiler.MainCompiler;

import java.util.Objects;

public class Compiler {

    public static final boolean RUN_AUTOTEST = false;   // default false
    public static final boolean MIPS_TEST = true;       // true: test MIPS, false: test PCODE

    private static final String[] tokenArgs     = new String[]{"-s", "testfile.txt", "-T", "output.txt"};
    private static final String[] syntaxArgs    = new String[]{"-s", "testfile.txt", "-S", "output.txt"};
    private static final String[] errorArgs     = new String[]{"-s", "testfile.txt", "-E", "error.txt"};
    private static final String[] mipsArgs      = new String[]{"-s", "testfile.txt", "-O", "mips.txt"};
    private static final String[] pcodeArgs     = new String[]{"-s", "testfile.txt", "-V", "pcoderesult.txt"};

    public static final String[] defaultArgs = mipsArgs;    // default: mipsArgs

    public static void runCompiler(String[] args) {
        assert args.length > 0;
        Config config = Config.fromArgs(args);
        if (Objects.isNull(config)) {
            System.err.println("Compiler failed launch due to previous errors.");
            System.out.println(Config.usage());
            System.exit(0);
        }
        try {
            MainCompiler compiler = new MainCompiler(config);
            compiler.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    public static void autoTest() {
        TestRunner runner = new TestRunner(MIPS_TEST ? TestRunner.Mode.MIPS : TestRunner.Mode.PCODE);
        runner.runAll();
    }

    public static void main(String[] args) {
        if (RUN_AUTOTEST) {
            autoTest();
        } else {
            runCompiler(args.length > 0 ? args : defaultArgs);
        }
    }
}
