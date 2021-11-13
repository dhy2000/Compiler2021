import backend.Mips;
import backend.Simulator;
import backend.Translator;
import backend.optimize.JumpFollow;
import config.Config;
import frontend.SysY;
import intermediate.Intermediate;
import intermediate.MidRunner;
import intermediate.optimize.MergeBlock;
import intermediate.optimize.PrintfTrans;
import intermediate.optimize.RemoveAfterJump;

import java.util.Objects;

public class Compiler {

    public static void main(String[] args) {
        // load arguments
        try {
            if (args.length > 0) { Config.loadArgs(args); }
            else { Config.loadArgs(new String[]{"-O", "-i", "testfile.txt", "-o", "mips.txt"}); }
            SysY sysy = new SysY(Config.getSource());
            Intermediate ir = sysy.getIntermediate();
            if (Objects.isNull(ir)) {
                return;
            }
            new PrintfTrans().optimize(ir); // NECESSARY! This is not an optimizer.

            /* ------ MidCode Optimize Begin ------ */
            new RemoveAfterJump().optimize(ir);
            new MergeBlock().optimize(ir);
            /* ------ MidCode Optimize End ------ */

            if (Config.hasOperationOutput(Config.Operation.INTERMEDIATE)) {
                ir.output(Config.getTarget());
            }
            if (Config.hasOperationOutput(Config.Operation.VIRTUAL_MACHINE)) {
                MidRunner vm = new MidRunner(ir);
                vm.run();
            }
            if (Config.hasOperationOutput(Config.Operation.OBJECT)) {
                Mips mips = new Translator(ir).toMips();

                /* ------ Mips Optimize Begin ------ */
                new JumpFollow().optimize(mips);
                /* ------ Mips Optimize End ------ */

                if (Config.hasOperationOutput(Config.Operation.RUN_OBJECT)) {
                    Simulator sim = new Simulator(mips);
                    sim.runAll(false);
                } else {
                    mips.output(Config.getTarget());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }
}
