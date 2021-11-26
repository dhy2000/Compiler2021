import autotest.TestRunner;
import compiler.Config;
import compiler.MainCompiler;

import java.util.Objects;

public class Compiler {

    public static final boolean RUN_AUTOTEST = true;
    public static final boolean MIPS = true;

    private static final String[] mipsArgs = new String[]{"-s", "testfile.txt", "-O", "mips.txt"};
    private static final String[] pcodeArgs = new String[]{"-s", "testfile.txt", "-V", "pcoderesult.txt"};

    public static void runCompiler(String[] args) {
        // load arguments
        String[] argsDefault = MIPS ? mipsArgs : pcodeArgs;
        Config config = Config.fromArgs(args.length > 0 ? args : argsDefault);
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
        TestRunner runner = new TestRunner(MIPS ? TestRunner.Mode.MIPS : TestRunner.Mode.PCODE);
        runner.runAll();
    }

    public static void main(String[] args) {
        if (RUN_AUTOTEST) {
            autoTest();
        } else {
            runCompiler(args);
        }
    }
}
