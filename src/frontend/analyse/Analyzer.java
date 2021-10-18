package frontend.analyse;

import frontend.symbol.FuncTable;
import frontend.symbol.SymTable;
import frontend.syntax.expr.multi.*;
import frontend.syntax.expr.unary.UnaryExp;
import intermediate.operand.Symbol;

/**
 * 语义分析器：遍历语法树，维护符号表，进行错误处理，生成中间代码（略）
 * 和语法分析类似的类递归下降结构
 *
 * 这里把每部分的分析器合到了一个大类中，因为要维护一个统一的栈（符号作用域）
 */
public class Analyzer {

    private final SymTable globalSymTable = SymTable.getGlobal();
    private final FuncTable funcTable = FuncTable.getInstance();

    /**
     * 表达式分析, 通常只会生成计算类型的中间代码
     */
    public static class ExpAnalyse {
        public static Symbol analyseCond(Cond cond) {
            return null;
        }

        public static Symbol analyseLOrExp(LOrExp exp) {
            return null;
        }

        public static Symbol analyseLAndExp(LAndExp exp) {
            return null;
        }

        public static Symbol analyseEqExp(EqExp exp) {
            return null;
        }

        public static Symbol analyseRelExp(RelExp exp) {
            return null;
        }

        public static Symbol analyseExp(Exp exp) {
            return null;
        }

        public static Symbol analyseAddExp(AddExp exp) {
            return null;
        }

        public static Symbol analyseMulExp(MulExp exp) {
            return null;
        }

        public static Symbol analyseUnaryExp(UnaryExp exp) {
            return null;
        }
    }

    /**
     * 语句和块的分析
     */
    public static class StmtAnalyse {

    }

    /**
     * 变量声明定义
     */
    public static class DeclAnalyse {

    }

    /**
     * 函数与编译单元
     */
    public static class FuncAnalyse {

    }
}
