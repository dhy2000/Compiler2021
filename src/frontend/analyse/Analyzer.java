package frontend.analyse;

import frontend.symbol.FuncTable;
import frontend.symbol.SymTable;
import frontend.syntax.decl.Decl;
import frontend.syntax.decl.Def;
import frontend.syntax.expr.multi.*;
import frontend.syntax.expr.unary.PrimaryExp;
import frontend.syntax.expr.unary.UnaryExp;
import frontend.syntax.func.FuncDef;
import frontend.syntax.stmt.complex.Block;
import frontend.syntax.stmt.complex.IfStmt;
import frontend.syntax.stmt.complex.WhileStmt;
import frontend.syntax.stmt.simple.*;
import intermediate.Intermediate;
import intermediate.code.BasicBlock;
import intermediate.operand.Operand;
import intermediate.operand.Symbol;

import java.util.Stack;

/**
 * 语义分析器：遍历语法树，维护符号表，进行错误处理，生成中间代码（略）
 * 和语法分析类似的类递归下降结构
 *
 * 这里把每部分的分析器合到了一个大类中，因为要维护一个统一的栈（符号作用域）
 */
public class Analyzer {

    private final SymTable globalSymTable = SymTable.getGlobal();
    private final FuncTable funcTable = FuncTable.getInstance();

    private final Intermediate intermediate = new Intermediate();

    private int blockCount = 0;
    private final Stack<BasicBlock> blockStack = new Stack<>();

    private BasicBlock getCurrentBlock() {
        return blockStack.peek();
    }

    private void pushBlock(BasicBlock block) {
        blockStack.push(block);
    }

    private BasicBlock popBlock() {
        return blockStack.pop();
    }

    /**
     * 表达式分析, 通常只会生成计算类型的中间代码
     */
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

    public static Operand analyseUnaryExp(UnaryExp exp) {
        return null;
    }

    public static Operand analysePrimaryExp(PrimaryExp exp) {
        return null;
    }

    /**
     * 语句和块的分析
     */
    /* ---- 简单语句 ---- */
    // 简单语句只会生成基本的中间代码（四元式条目），只需追加到当前的块中即可
    public static void analyseAssignStmt(AssignStmt stmt) {}

    public static void analyseExpStmt(ExpStmt stmt) {}

    public static void analyseInputStmt(InputStmt stmt) {}

    public static void analyseOutputStmt(OutputStmt stmt) {}

    public static void analyseReturnStmt(ReturnStmt stmt) {}

    public static void analyseBreakStmt(BreakStmt stmt) {}

    public static void analyseContinueStmt(ContinueStmt stmt) {}
    /* ---- 复杂语句 ---- */
    // 这部分语句会产生新的基本块，以及更深嵌套的符号表
    public static void analyseIfStmt(IfStmt stmt) {}

    public static void analyseWhileStmt(WhileStmt stmt) {}

    public static BasicBlock analyseBlock(Block stmt, String name) {
        return null;
    }


    /**
     * 变量声明定义
     */
    public static void analyseDecl(Decl decl) {}

    public static void analyseDef(Def def) {}


    /**
     * 函数与编译单元
     */
    public static void analyseFunc(FuncDef func) {}
}
