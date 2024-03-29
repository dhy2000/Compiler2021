package compiler;

import backend.Mips;
import backend.Translator;
import backend.optimize.JumpFollow;
import frontend.SysY;
import middle.MidRunner;
import middle.MiddleCode;
import middle.optimize.*;

import java.util.Objects;

public class MainCompiler {
    private final Config config;

    public MainCompiler(Config config) {
        this.config = config;
    }

    public void run() {
        try {
            SysY sysy = new SysY(config);
            MiddleCode ir = sysy.getIntermediate();
            if (Objects.isNull(ir)) {
                return;
            }
            /* ------ MidCode Optimize Begin ------ */
            new RemoveAfterJump().optimize(ir);
            new MergeBlock().optimize(ir);
            new MulDivOpt().optimize(ir);
            /* ------ MidCode Optimize End ------ */

            if (config.hasTarget(Config.Operation.MID_CODE)) {
                ir.output(config.getTarget(Config.Operation.MID_CODE));
            }
            if (config.hasTarget(Config.Operation.VM_RUNNER)) {
                MidRunner vm = new MidRunner(ir, config.getInput(), config.getTarget(Config.Operation.VM_RUNNER));
                vm.run();
            }

            if (config.hasTarget(Config.Operation.OBJECT_CODE)) {
                Mips mips;
                if (SpecialOptimize.ENABLE_SPECIAL_OPTIMIZE) {
                    mips = sysy.getSpecialMips();
                }
                if (Objects.isNull(mips)) {
                    mips = new Translator(ir).toMips();
                }

                /* ------ Mips Optimize Begin ------ */
                new JumpFollow().optimize(mips);
                /* ------ Mips Optimize End ------ */

                mips.output(config.getTarget(Config.Operation.OBJECT_CODE));
            }
        } catch (Exception e) {
            // Can detect exception here!
            if (config.hasTarget(Config.Operation.EXCEPTION)) {
                config.getTarget(Config.Operation.EXCEPTION).println(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace(config.getTarget(Config.Operation.EXCEPTION));
            } else {
                System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
