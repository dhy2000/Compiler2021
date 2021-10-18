package frontend.analyse;

import frontend.lexical.token.Token;
import frontend.symbol.FuncTable;
import frontend.symbol.SymTable;
import frontend.syntax.Component;
import frontend.syntax.decl.Decl;
import frontend.syntax.decl.Def;
import frontend.syntax.expr.multi.*;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;
import frontend.syntax.func.FuncDef;
import frontend.syntax.stmt.complex.Block;
import frontend.syntax.stmt.complex.IfStmt;
import frontend.syntax.stmt.complex.WhileStmt;
import frontend.syntax.stmt.simple.*;
import intermediate.Intermediate;
import intermediate.code.BasicBlock;
import intermediate.code.BinaryOp;
import intermediate.code.UnaryOp;
import intermediate.operand.Immediate;
import intermediate.operand.Operand;
import intermediate.operand.Symbol;

import java.util.Iterator;
import java.util.Objects;
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
        assert !blockStack.empty();
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
    private BinaryOp.Op tokenToBinaryOp(Token token) {
        switch (token.getType()) {
            case PLUS: return BinaryOp.Op.ADD;
            case MINU: return BinaryOp.Op.SUB;
            case MULT: return BinaryOp.Op.MUL;
            case DIV: return BinaryOp.Op.DIV;
            case MOD: return BinaryOp.Op.MOD;
            case AND: return BinaryOp.Op.AND;
            case OR: return BinaryOp.Op.OR;
            case GEQ: return BinaryOp.Op.GE;
            case GRE: return BinaryOp.Op.GT;
            case LEQ: return BinaryOp.Op.LE;
            case LSS: return BinaryOp.Op.LT;
            case EQL: return BinaryOp.Op.EQ;
            case NEQ: return BinaryOp.Op.NE;
            default: return null;
        }
    }

    private UnaryOp.Op tokenToUnaryOp(Token token) {
        switch (token.getType()) {
            case PLUS: return UnaryOp.Op.MOV;
            case MINU: return UnaryOp.Op.NEG;
            case NOT: return UnaryOp.Op.NOT;
            default: return null;
        }
    }

    public Operand analyseBinaryExp(MultiExp<?> exp) {
        Component first = exp.getFirst();
        Operand ret;
        if (first instanceof MultiExp) {
            ret = analyseBinaryExp((MultiExp<?>) first);
        } else if (first instanceof UnaryExp) {
            ret = analyseUnaryExp((UnaryExp) first);
        } else {
            throw new AssertionError("MultiExp<Component> is not MultiExp or UnaryExp");
        }
        Iterator<Token> iterOp = exp.iterOperator();
        Iterator<?> iterSrc = exp.iterOperand();
        while (iterOp.hasNext() && iterSrc.hasNext()) {
            Token op = iterOp.next();
            Object src = iterSrc.next();
            Operand subResult;
            if (src instanceof MultiExp) {
                subResult = analyseBinaryExp((MultiExp<?>) src);
            } else if (src instanceof UnaryExp) {
                subResult = analyseUnaryExp((UnaryExp) src);
            } else {
                throw new AssertionError("MultiExp<Component> is not MultiExp or UnaryExp");
            }
            Symbol sym = Symbol.temporary();
            BinaryOp ir = new BinaryOp(tokenToBinaryOp(op), ret, subResult, sym);
            getCurrentBlock().append(ir);
            ret = sym;
        }
        return ret;
    }

    public Operand analyseUnaryExp(UnaryExp exp) {
        BaseUnaryExp base = exp.getBase();
        Operand result = null;
        if (base instanceof FunctionCall) {
            // TODO: 查符号表, 确认参数，传递参数，参数不匹配错误
        } else if (base instanceof PrimaryExp) {
            result = analysePrimaryExp((PrimaryExp) base);
        }
        assert Objects.nonNull(result); // null means void function return
        Iterator<Token> iterUnaryOp = exp.iterUnaryOp();
        while (iterUnaryOp.hasNext()) {
            Token op = iterUnaryOp.next();
            Symbol tmp = Symbol.temporary();
            UnaryOp ir = new UnaryOp(tokenToUnaryOp(op), result, tmp);
            getCurrentBlock().append(ir);
            result = tmp;
        }
        return result;
    }

    public Operand analysePrimaryExp(PrimaryExp exp) {
        BasePrimaryExp base = exp.getBase();
        if (base instanceof SubExp) {
            // TODO: 括号匹配错误
        } else if (base instanceof LVal) {
            // TODO: 符号表相关错误(变量未定义等)
        } else if (base instanceof Number) {
            return new Immediate(((Number) base).getValue().getValue());
        } else {
            throw new AssertionError("BasePrimaryExp type error!");
        }
        return null;
    }

    /**
     * 语句和块的分析
     */
    /* ---- 简单语句 ---- */
    // 简单语句只会生成基本的中间代码（四元式条目），只需追加到当前的块中即可
    public void analyseAssignStmt(AssignStmt stmt) {}

    public void analyseExpStmt(ExpStmt stmt) {}

    public void analyseInputStmt(InputStmt stmt) {}

    public void analyseOutputStmt(OutputStmt stmt) {}

    public void analyseReturnStmt(ReturnStmt stmt) {}

    public void analyseBreakStmt(BreakStmt stmt) {}

    public void analyseContinueStmt(ContinueStmt stmt) {}
    /* ---- 复杂语句 ---- */
    // 这部分语句会产生新的基本块，以及更深嵌套的符号表
    public void analyseIfStmt(IfStmt stmt) {}

    public void analyseWhileStmt(WhileStmt stmt) {}

    public BasicBlock analyseBlock(Block stmt, String name) {
        return null;
    }


    /**
     * 变量声明定义
     */
    public void analyseDecl(Decl decl) {}

    public void analyseDef(Def def) {}


    /**
     * 函数与编译单元
     */
    public void analyseFunc(FuncDef func) {}
}
