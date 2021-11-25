package compiler;

import backend.Mips;
import backend.Translator;
import backend.optimize.JumpFollow;
import frontend.SysY;
import middle.MidRunner;
import middle.MiddleCode;
import middle.optimize.MergeBlock;
import middle.optimize.PrintfTrans;
import middle.optimize.RemoveAfterJump;

import java.util.Objects;

public class MainCompiler {
    private final Config config;

    public MainCompiler(Config config) {
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
}