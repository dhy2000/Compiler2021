package frontend.analyse;

import frontend.symbol.FuncTable;
import frontend.symbol.SymTable;

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
     * 表达式分析
     */
    public static class ExpAnalyse {

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
