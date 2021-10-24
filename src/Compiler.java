import config.Config;
import frontend.SysY;
import intermediate.Intermediate;
import intermediate.MidRunner;
import intermediate.optimize.PrintfTrans;

import java.util.Objects;

public class Compiler {

    public static void main(String[] args) {
        // load arguments
        try {
            if (args.length > 0) { Config.loadArgs(args); }
            else { Config.loadArgs(new String[]{"-E", "-i", "testfile.txt", "-o", "error.txt"}); }
            SysY sysy = new SysY(Config.getSource());
            Intermediate ir = sysy.getIntermediate();
            if (Objects.isNull(ir)) {
                return;
            }
            new PrintfTrans().optimize(ir);
            if (Config.hasOperationOutput(Config.Operation.INTERMEDIATE)) {
                ir.output(Config.getTarget());
            }
            if (Config.hasOperationOutput(Config.Operation.VIRTUAL_MACHINE)) {
                MidRunner vm = new MidRunner(ir);
                vm.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }
}
