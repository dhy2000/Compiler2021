import compiler.Config;
import compiler.MainCompiler;

import java.util.Objects;

public class Compiler {

    public static void main(String[] args) {
        // load arguments
        String[] argsDefault = new String[]{"-s", "testfile.txt", "-O", "mips.txt"};
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
}
