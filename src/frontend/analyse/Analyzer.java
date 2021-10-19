package frontend.analyse;

import frontend.error.Error;
import frontend.error.ErrorTable;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.decl.Decl;
import frontend.syntax.decl.Def;
import frontend.syntax.expr.multi.Cond;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MultiExp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;
import frontend.syntax.func.FuncDef;
import frontend.syntax.stmt.complex.Block;
import frontend.syntax.stmt.complex.IfStmt;
import frontend.syntax.stmt.complex.WhileStmt;
import frontend.syntax.stmt.simple.*;
import intermediate.Intermediate;
import intermediate.code.*;
import intermediate.operand.Immediate;
import intermediate.operand.Operand;
import intermediate.symbol.FuncMeta;
import intermediate.symbol.SymTable;
import intermediate.symbol.Symbol;

import java.util.*;

/**
 * 语义分析器：遍历语法树，维护符号表，进行错误处理，生成中间代码（略）
 * 和语法分析类似的类递归下降结构
 *
 * 这里把每部分的分析器合到了一个大类中，因为要维护一个统一的栈（符号作用域）
 */
public class Analyzer {

    private SymTable currentSymTable = SymTable.global();   // 栈式符号表

    private final Intermediate intermediate = new Intermediate(); // 最终生成的中间代码

    private int blockCount = 0;
    private final Stack<BasicBlock> blockStack = new Stack<>();

    private BasicBlock currentBlock() {
        return blockStack.peek();
    }

    private String currentField() {
        return currentSymTable.getField();
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

    public Operand analyseCond(Cond cond) {
        return analyseBinaryExp(cond.getLOrExp());
    }

    public Operand analyseExp(Exp exp) {
        return analyseBinaryExp(exp.getAddExp());
    }

    private Operand analyseBinaryOrUnaryExp(Component item) {
        if (item instanceof MultiExp) {
            return analyseBinaryExp((MultiExp<?>) item);
        } else if (item instanceof UnaryExp) {
            return analyseUnaryExp((UnaryExp) item);
        } else {
            throw new AssertionError("Wrong Component");
        }
    }

    /**
     * 分析二元表达式，生成中间代码并返回作为表达式运算结果的符号
     * @param exp 二元表达式根节点
     * @return 作为该表达式运算结果的符号
     */
    public Operand analyseBinaryExp(MultiExp<?> exp) {
        Component first = exp.getFirst();
        Operand ret = analyseBinaryOrUnaryExp(first);
        if (Objects.isNull(ret)) {
            return null;
        }
        Iterator<Token> iterOp = exp.iterOperator();
        Iterator<?> iterSrc = exp.iterOperand();
        while (iterOp.hasNext() && iterSrc.hasNext()) {
            Token op = iterOp.next();
            Component src = (Component) iterSrc.next();
            Operand subResult = analyseBinaryOrUnaryExp(src);
            if (Objects.isNull(subResult)) {
                return null;
            }
            Symbol temp = Symbol.temporary(currentField(), Symbol.Type.INT);
            currentBlock().append(new BinaryOp(tokenToBinaryOp(op), ret, subResult, temp));
            ret = temp;
        }
        return ret;
    }

    /**
     * 分析一元表达式, 生成中间代码并返回结果
     * @param exp 一元表达式
     * @return 作为表达式结果的符号, 如果是 void 函数，则返回 null
     */
    public Operand analyseUnaryExp(UnaryExp exp) {
        BaseUnaryExp base = exp.getBase();
        Operand result = null;
        if (base instanceof FunctionCall) {
            // 查符号表, 确认参数，传递参数，参数不匹配错误
            // 如果调用了 void 函数，返回 null
            FunctionCall call = (FunctionCall) base;
            Ident ident = call.getName();
            String name = ident.getName();
            if (!intermediate.getFunctions().containsKey(name)) {
                ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, ident.lineNumber()));
                return new Immediate(0);
            }
            FuncMeta func = intermediate.getFunctions().get(name);
            // match arguments
            List<Operand> params = new ArrayList<>();
            FuncRParams rParams = call.getParams();
            Iterator<Exp> iter = rParams.iterParams();
            List<Symbol> args = func.getParams();
            Iterator<Symbol> iterArgs = args.listIterator();
            while (iter.hasNext() && iterArgs.hasNext()) {
                Exp p = iter.next();
                Operand r = analyseExp(p);
                Symbol arg = iterArgs.next();
                if (Objects.isNull(r)) {
                    // returning void
                    ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                    break;
                } else if (r instanceof Immediate) {
                    // Integer
                    if (!arg.getType().equals(Symbol.Type.INT)) {
                        ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                        break;
                    } else {
                        params.add(r);
                    }
                } else {
                    assert r instanceof Symbol;
                    if (((Symbol) r).getType().equals(arg.getType())) {
                        params.add(r);
                    } else {
                        ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                        break;
                    }
                }
            }
            boolean f1 = iter.hasNext();
            boolean f2 = iterArgs.hasNext();
            boolean error = false;
            if (f1 && f2) {
                // MISMATCH TYPE
                error = true;
            } else if (f1 || f2) {
                ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_NUM, ident.lineNumber()));
                error = true;
            }
            if (func.getReturnType().equals(FuncMeta.ReturnType.VOID)) {
                if (!error) {
                    currentBlock().append(new Call(func, params));
                }
                return null;
            } else {
                if (!error) {
                    Symbol r = Symbol.temporary(currentField(), Symbol.Type.INT);
                    currentBlock().append(new Call(func, params, r));
                    return r;
                } else {
                    return new Immediate(0);
                }
            }
        } else if (base instanceof PrimaryExp) {
            result = analysePrimaryExp((PrimaryExp) base);
        }
        assert Objects.nonNull(result); // null means void function return
        Iterator<Token> iterUnaryOp = exp.iterUnaryOp();
        while (iterUnaryOp.hasNext()) {
            Token op = iterUnaryOp.next();
            Symbol tmp = Symbol.temporary(currentField(), Symbol.Type.INT);
            UnaryOp ir = new UnaryOp(tokenToUnaryOp(op), result, tmp);
            currentBlock().append(ir);
            result = tmp;
        }
        return result;
    }

    /**
     * 分析基础一元表达式 (子表达式, 左值，字面量）
     * @param exp 一元表达式
     * @return 表达式结果对应的符号
     */
    public Operand analysePrimaryExp(PrimaryExp exp) {
        BasePrimaryExp base = exp.getBase();
        if (base instanceof SubExp) {
            SubExp sub = (SubExp) base;
            if (!((SubExp) base).hasRightParenthesis()) {
                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, sub.getLeftParenthesis().lineNumber()));
            }
            return analyseExp(sub.getExp());
        } else if (base instanceof LVal) {
            // 符号表相关错误(变量未定义等)
            LVal val = (LVal) base;
            if (currentSymTable.contains(val.getName().getName(), true)) {
                ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, val.getName().lineNumber()));
                return null;
            }
            // 缺中括号错误
            Iterator<LVal.Index> iterIndex = val.iterIndexes();
            List<Operand> indexes = new ArrayList<>();
            while (iterIndex.hasNext()) {
                LVal.Index index = iterIndex.next();
                if (!index.hasRightBracket()) {
                    ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, index.getLeftBracket().lineNumber()));
                    return null;
                }
                indexes.add(analyseExp(index.getIndex()));
            }
            Operand offsetBase = new Immediate(1);
            Operand offset = new Immediate(0);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                // offset += arrayIndexes[i] * baseOffset;
                Symbol prod = Symbol.temporary(currentField(), Symbol.Type.INT);
                currentBlock().append(new BinaryOp(BinaryOp.Op.MUL, indexes.get(i), offsetBase, prod));
                Symbol sum = Symbol.temporary(currentField(), Symbol.Type.INT);
                currentBlock().append(new BinaryOp(BinaryOp.Op.ADD, offset, prod, sum));
                offset = sum;
            }
            Symbol symbol = currentSymTable.get(val.getName().getName(), true);
            if (symbol.getType().equals(Symbol.Type.INT)) {
                return symbol;
            } else {
                // ARRAY or POINTER
                Symbol temp = Symbol.temporary(currentField(), Symbol.Type.POINTER);
                currentBlock().append(new AddressOp(symbol, offset, temp));
                return temp;
            }
        } else if (base instanceof Number) {
            return new Immediate(((Number) base).getValue().getValue());
        } else {
            throw new AssertionError("BasePrimaryExp type error!");
        }
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
