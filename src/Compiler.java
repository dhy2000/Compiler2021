import config.Config;
import frontend.SysY;

public class Compiler {

    public static void main(String[] args) {
        // load arguments
        try {
            if (args.length > 0) { Config.loadArgs(args); }
            else { Config.loadArgs(new String[]{"-E", "-i", "testfile.txt", "-o", "error.txt"}); }
            SysY sysy = new SysY(Config.getSource());
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }
}
