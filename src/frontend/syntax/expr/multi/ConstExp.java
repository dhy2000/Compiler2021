package frontend.syntax.expr.multi;

import java.io.PrintStream;

/**
 * 常量表达式, 要求用到的变量必须是常量
 */
public class ConstExp extends Exp {
    public ConstExp(AddExp addExp) {
        super(addExp);
    }

    @Override
    public void output(PrintStream ps) {
        getAddExp().output(ps);
        ps.println("<ConstExp>");
    }
}
