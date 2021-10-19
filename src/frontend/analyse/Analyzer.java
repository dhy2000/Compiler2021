package frontend.analyse;

import frontend.lexical.token.Token;
import frontend.symbol.FuncTable;
import frontend.symbol.SymTable;
import frontend.syntax.decl.Decl;
import frontend.syntax.decl.Def;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MultiExp;
import frontend.syntax.expr.unary.LVal;
import frontend.syntax.expr.unary.PrimaryExp;
import frontend.syntax.expr.unary.UnaryExp;
import frontend.syntax.func.FuncDef;
import frontend.syntax.stmt.complex.Block;
import frontend.syntax.stmt.complex.IfStmt;
import frontend.syntax.stmt.complex.WhileStmt;
import frontend.syntax.stmt.simple.*;
import intermediate.Intermediate;
import intermediate.code.BasicBlock;
import intermediate.code.BinaryOp;
import intermediate.code.UnaryOp;
import intermediate.operand.Operand;

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

    private enum BlockType {
        FUNC, IF, WHILE
    }

    private final Stack<BlockType> blockTypeStack = new Stack<>();
//    private final Stack<SymTable> symbolStack = new Stack<>();

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

//    private SymTable getCurrentSymTable() {
//        assert !symbolStack.empty();
//        return symbolStack.peek();
//    }
//
//    private void pushSymTable(SymTable table) {
//        symbolStack.push(table);
//    }
//
//    private SymTable popSymTable() {
//        return symbolStack.pop();
//    }

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

    public Operand analyseExp(Exp exp) {
        return analyseBinaryExp(exp.getAddExp());
    }

    public Operand analyseBinaryExp(MultiExp<?> exp) {
//        Component first = exp.getFirst();
//        Operand ret;
//        if (first instanceof MultiExp) {
//            ret = analyseBinaryExp((MultiExp<?>) first);
//        } else if (first instanceof UnaryExp) {
//            ret = analyseUnaryExp((UnaryExp) first);
//        } else {
//            throw new AssertionError("MultiExp<Component> is not MultiExp or UnaryExp");
//        }
//        Iterator<Token> iterOp = exp.iterOperator();
//        Iterator<?> iterSrc = exp.iterOperand();
//        while (iterOp.hasNext() && iterSrc.hasNext()) {
//            Token op = iterOp.next();
//            Object src = iterSrc.next();
//            Operand subResult;
//            if (src instanceof MultiExp) {
//                subResult = analyseBinaryExp((MultiExp<?>) src);
//            } else if (src instanceof UnaryExp) {
//                subResult = analyseUnaryExp((UnaryExp) src);
//            } else {
//                throw new AssertionError("MultiExp<Component> is not MultiExp or UnaryExp");
//            }
//            Symbol sym = Symbol.temporary();
//            BinaryOp ir = new BinaryOp(tokenToBinaryOp(op), ret, subResult, sym);
//            getCurrentBlock().append(ir);
//            ret = sym;
//        }
//        return ret;
        return null;
    }

    public Operand analyseUnaryExp(UnaryExp exp) {
//        BaseUnaryExp base = exp.getBase();
//        Operand result = null;
//        if (base instanceof FunctionCall) {
//            // TODO: 查符号表, 确认参数，传递参数，参数不匹配错误
//            // TODO: 如果调用了 void 函数，返回 null
//        } else if (base instanceof PrimaryExp) {
//            result = analysePrimaryExp((PrimaryExp) base);
//        }
//        assert Objects.nonNull(result); // null means void function return
//        Iterator<Token> iterUnaryOp = exp.iterUnaryOp();
//        while (iterUnaryOp.hasNext()) {
//            Token op = iterUnaryOp.next();
//            Symbol tmp = Symbol.temporary();
//            UnaryOp ir = new UnaryOp(tokenToUnaryOp(op), result, tmp);
//            getCurrentBlock().append(ir);
//            result = tmp;
//        }
//        return result;
        return null;
    }

    public Operand analysePrimaryExp(PrimaryExp exp) {
//        BasePrimaryExp base = exp.getBase();
//        if (base instanceof SubExp) {
//            SubExp sub = (SubExp) base;
//            if (!((SubExp) base).hasRightParenthesis()) {
//                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, sub.getLeftParenthesis().lineNumber()));
//            }
//            return analyseExp(sub.getExp());
//        } else if (base instanceof LVal) {
//            // TODO: 符号表相关错误(变量未定义等)
//            LVal val = (LVal) base;
//            if (getCurrentSymTable().contains(val.getName().getName())) {
//                ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, val.getName().lineNumber()));
//                return new Immediate(0);
//            }
//            // TODO: 缺中括号错误
//            Iterator<LVal.Index> iterIndex = val.iterIndexes();
//            List<Operand> indexes = new ArrayList<>();
//            while (iterIndex.hasNext()) {
//                LVal.Index index = iterIndex.next();
//                if (!index.hasRightBracket()) {
//                    ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, index.getLeftBracket().lineNumber()));
//                    return new Immediate(0);
//                }
//                indexes.add(analyseExp(index.getIndex()));
//            }
//            Operand offsetBase = new Immediate(1);
//            Operand offset = new Immediate(0);
//            for (int i = indexes.size() - 1; i >= 0; i--) {
//                // offset += arrayIndexes[i] * baseOffset;
//                Symbol prod = Symbol.temporary();
//                getCurrentBlock().append(new BinaryOp(BinaryOp.Op.MUL, indexes.get(i), offsetBase, prod));
//                Symbol sum = Symbol.temporary();
//                getCurrentBlock().append(new BinaryOp(BinaryOp.Op.ADD, offset, prod, sum));
//                offset = sum;
//            }
//            SymTable.Item item = getCurrentSymTable().getItemByName(val.getName().getName());
//            Symbol symbol = item.getInterSymbol();
//            if (symbol.getType().equals(Symbol.Type.INT)) {
//                return symbol;
//            } else {
//                Symbol value = Symbol.temporary();
//                getCurrentBlock().append(new ArrayOp(ArrayOp.Op.LOAD, symbol, offset, value));
//                return value;
//            }
//
//        } else if (base instanceof Number) {
//            return new Immediate(((Number) base).getValue().getValue());
//        } else {
//            throw new AssertionError("BasePrimaryExp type error!");
//        }
        return null;
    }

    /**
     * 语句和块的分析
     */
    /* ---- 简单语句 ---- */
    // 简单语句只会生成基本的中间代码（四元式条目），只需追加到当前的块中即可
    public void analyseAssignStmt(AssignStmt stmt) {
        // 区分左边是数组还是变量
        // MOV 指令
    }

    public void analyseExpStmt(ExpStmt stmt) {
        // 这个最容易
    }

    public void analyseInputStmt(InputStmt stmt) {
        LVal left = stmt.getLeftVal();
        // TODO: 检查符号表，检查变量类型
    }

    public void analyseOutputStmt(OutputStmt stmt) {
        // TODO: 检查 FormatString, 检查参数和格式符的个数(以及类型)
        // TODO: 生成输出语句
    }

    public void analyseReturnStmt(ReturnStmt stmt) {
        // TODO: return 语句类型和当前函数的类型是不是匹配
    }

    public void analyseBreakStmt(BreakStmt stmt) {
        // TODO: 就是一个跳转，跳到往上的循环的下一层
    }

    public void analyseContinueStmt(ContinueStmt stmt) {
        // TODO: 也是一个跳转，跳到往上的循环的头
    }
    /* ---- 复杂语句 ---- */
    // 这部分语句会产生新的基本块，以及更深嵌套的符号表
    public void analyseIfStmt(IfStmt stmt, BasicBlock follow) {
        // TODO: 缺右括号
        // TODO: 生成新的基本块
    }

    public void analyseWhileStmt(WhileStmt stmt, BasicBlock follow) {
        // TODO: 缺右括号
        // TODO: 生成新的基本块
    }

    public BasicBlock analyseBlock(Block stmt, String name) {
        // TODO: 一条一条语句去遍历就行
        return null;
    }

    /**
     * 变量声明定义
     */
    public void analyseDecl(Decl decl) {
        // TODO: 这部分好像没啥东西，主要是看是不是常量
    }

    public void analyseDef(Def def) {
        // TODO: 维护符号表，重定义错误
    }


    /**
     * 函数与编译单元
     */
    public void analyseFunc(FuncDef func) {
        // 维护函数符号表
    }
}
