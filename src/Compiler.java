import backend.Mips;
import backend.Translator;
import backend.optimize.JumpFollow;
import config.Config;
import frontend.SysY;
import middle.MiddleCode;
import middle.MidRunner;
import middle.optimize.MergeBlock;
import middle.optimize.PrintfTrans;
import middle.optimize.RemoveAfterJump;

import java.util.Objects;

public class Compiler {

    private final Config config;

    public Compiler(Config config) {
        this.config = config;
    }

    public void run() throws Exception {
        SysY sysy = new SysY(config);
        MiddleCode ir = sysy.getIntermediate();
        if (Objects.isNull(ir)) {
            return;
        }
        new PrintfTrans().optimize(ir); // NECESSARY transformer! This is NOT an optimizer.

        /* ------ MidCode Optimize Begin ------ */
        new RemoveAfterJump().optimize(ir);
        new MergeBlock().optimize(ir);
        /* ------ MidCode Optimize End ------ */

        if (config.hasTarget(Config.Operation.MID_CODE)) {
            ir.output(config.getTarget(Config.Operation.MID_CODE));
        }
        if (config.hasTarget(Config.Operation.VM_RUNNER)) {
            MidRunner vm = new MidRunner(ir, config.getInput(), config.getTarget(Config.Operation.VM_RUNNER));
            vm.run();
        }

        if (config.hasTarget(Config.Operation.OBJECT_CODE)) {
            Mips mips = new Translator(ir).toMips();

            /* ------ Mips Optimize Begin ------ */
            new JumpFollow().optimize(mips);
            /* ------ Mips Optimize End ------ */

            mips.output(config.getTarget(Config.Operation.OBJECT_CODE));
        }
    }

    public static void main(String[] args) {
        // load arguments
        try {
            String[] argsDefault = new String[]{"-s", "testfile.txt", "-O", "mips.txt"};
            Config config = Config.fromArgs(args.length > 0 ? args : argsDefault);
            if (Objects.isNull(config)) {
                System.err.println("Compiler failed launch due to previous errors.");
                System.out.println(Config.usage());
                System.exit(0);
            }
            Compiler compiler = new Compiler(config);
            compiler.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }
}
