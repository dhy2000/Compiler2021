package frontend.visitor;

import exception.ConstExpException;
import exception.VarAtConstException;
import frontend.error.Error;
import frontend.error.ErrorTable;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.CompUnit;
import frontend.syntax.Component;
import frontend.syntax.decl.*;
import frontend.syntax.expr.multi.*;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;
import frontend.syntax.func.FuncDef;
import frontend.syntax.func.FuncFParam;
import frontend.syntax.func.FuncFParams;
import frontend.syntax.func.MainFuncDef;
import frontend.syntax.stmt.Stmt;
import frontend.syntax.stmt.complex.*;
import frontend.syntax.stmt.simple.*;
import middle.MiddleCode;
import middle.code.*;
import middle.operand.Immediate;
import middle.operand.Operand;
import middle.symbol.FuncMeta;
import middle.symbol.SymTable;
import middle.symbol.Symbol;

import java.util.*;

/**
 * 语义分析器：遍历语法树，维护符号表，进行错误处理，生成中间代码
 * 和语法分析类似的类递归下降结构
 *
 * 这里把每部分的分析器合到了一个大类中，因为要维护一个统一的栈（符号作用域）
 */
public class Visitor {

    private SymTable currentSymTable = SymTable.global();   // 栈式符号表

    private final MiddleCode middleCode = new MiddleCode(); // 最终生成的中间代码

    public MiddleCode getIntermediate() {
        return middleCode;
    }

    private final ErrorTable errorTable = new ErrorTable();

    public ErrorTable getErrorTable() {
        return errorTable;
    }

    private FuncMeta currentFunc = null;

    private int blockCount = 0;
    private int blockDepth = 0;

    private int newBlockCount() {
        blockCount += 1;
        return blockCount;
    }

    private BasicBlock currentBlock;
    private final Stack<BasicBlock> loopBlocks = new Stack<>();
    private final Stack<BasicBlock> loopFollows = new Stack<>();
    private int stackSize = 0;

    public Visitor() {}

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
            case AND: return BinaryOp.Op.ANDL;
            case OR: return BinaryOp.Op.ORL;
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
        return analyseLOrExp(cond.getLOrExp());
    }

    // 短路求值! 前一项如果为 True 就不用算后面的项了
    public Operand analyseLOrExp(LOrExp exp) {
        BasicBlock orFollow = new BasicBlock("COND_OR_" + newBlockCount(), BasicBlock.Type.BASIC);
        LAndExp first = exp.getFirst();
        Symbol or = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM); // or result
        Operand and = analyseLAndExp(first);
        if (Objects.isNull(and)) {
            return null;
        }
        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, and, or));
        BasicBlock next = new BasicBlock("OR_AND_" + newBlockCount(), BasicBlock.Type.BASIC);
        currentBlock.append(new BranchIfElse(or, orFollow, next));
        currentBlock = next;
        Iterator<LAndExp> iter = exp.iterOperand();
        while (iter.hasNext()) {
            LAndExp andExp = iter.next();
            and = analyseLAndExp(andExp);
            if (Objects.isNull(and)) {
                return null;
            }
            currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, and, or));
            next = new BasicBlock("OR_AND_" + newBlockCount(), BasicBlock.Type.BASIC);
            currentBlock.append(new BranchIfElse(or, orFollow, next));
            currentBlock = next;
        }
        currentBlock.append(new Jump(orFollow));
        currentBlock = orFollow;
        return or;
    }

    public Operand analyseLAndExp(LAndExp exp) {
        BasicBlock andFollow = new BasicBlock("COND_AND_" + newBlockCount(), BasicBlock.Type.BASIC);
        EqExp first = exp.getFirst();
        Symbol and = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM); // and result
        Operand item = analyseBinaryExp(first);
        if (Objects.isNull(item)) {
            return null;
        }
        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, item, and));
        BasicBlock next = new BasicBlock("AND_ITEM_" + newBlockCount(), BasicBlock.Type.BASIC);
        currentBlock.append(new BranchIfElse(and, next, andFollow));
        currentBlock = next;
        Iterator<EqExp> iter = exp.iterOperand();
        while (iter.hasNext()) {
            EqExp eqExp = iter.next();
            item = analyseBinaryExp(eqExp);
            if (Objects.isNull(item)) {
                return null;
            }
            currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, item, and));
            next = new BasicBlock("AND_ITEM_" + newBlockCount(), BasicBlock.Type.BASIC);
            currentBlock.append(new BranchIfElse(and, next, andFollow));
            currentBlock = next;
        }
        currentBlock.append(new Jump(andFollow));
        currentBlock = andFollow;
        return and;
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
            Symbol temp = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
            currentBlock.append(new BinaryOp(tokenToBinaryOp(op), ret, subResult, temp));
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
            if (!call.hasRightParenthesis()) {
                errorTable.add(new Error(Error.Type.MISSING_RIGHT_PARENT, ident.lineNumber()));
            }
            if (!middleCode.getFunctions().containsKey(name)) {
                errorTable.add(new Error(Error.Type.UNDEFINED_IDENT, ident.lineNumber()));
                return new Immediate(0);
            }
            FuncMeta func = middleCode.getFunctions().get(name);
            // match arguments
            List<Operand> params = new ArrayList<>();
            List<Symbol> args = func.getParams();
            if (call.hasParams()) {
                FuncRParams rParams = call.getParams();
                Exp firstExp = rParams.getFirst();
                Operand firstParam = analyseExp(firstExp);
                params.add(firstParam);
                Iterator<Exp> iter = rParams.iterParams();
                while (iter.hasNext()) {
                    Exp p = iter.next();
                    Operand r = analyseExp(p);
                    params.add(r);
                }
            }
            boolean error = false;
            if (params.size() != args.size()) {
                errorTable.add(new Error(Error.Type.MISMATCH_PARAM_NUM, ident.lineNumber()));
                error = true;
            } else {
                Iterator<Operand> iterParam = params.listIterator();
                Iterator<Symbol> iterArg = args.listIterator();
                while (iterParam.hasNext() && iterArg.hasNext()) {
                    Operand param = iterParam.next();
                    Symbol arg = iterArg.next();
                    if (Objects.isNull(param)) {
                        errorTable.add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                        error = true;
                        break;
                    }
                    else if (param instanceof Immediate) {
                        if (!arg.getRefType().equals(Symbol.RefType.ITEM)) {
                            errorTable.add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                            error = true;
                            break;
                        }
                    }
                    else {
                        assert param instanceof Symbol;
                        if (!((Symbol) param).getRefType().equals(arg.getRefType())) {
                            errorTable.add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                            error = true;
                            break;
                        }
                    }
                }
            }
            // check argument match

            if (func.getReturnType().equals(FuncMeta.ReturnType.VOID)) {
                if (!error) {
                    currentBlock.append(new Call(func, params));
                }
                return null;
            } else {
                if (!error) {
                    Symbol r = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
                    currentBlock.append(new Call(func, params, r));
                    result = r;
                } else {
                    return new Immediate(0);
                }
            }
        } else if (base instanceof PrimaryExp) {
            result = analyseBasePrimaryExp(((PrimaryExp) base).getBase(), false);
        }
        assert Objects.nonNull(result); // null means void function return
        Iterator<Token> iterUnaryOp = exp.iterUnaryOp();
        while (iterUnaryOp.hasNext()) {
            Token op = iterUnaryOp.next();
            Symbol tmp = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
            UnaryOp ir = new UnaryOp(tokenToUnaryOp(op), result, tmp);
            currentBlock.append(ir);
            result = tmp;
        }
        return result;
    }

    /**
     * 分析基础一元表达式 (子表达式, 左值，字面量）
     * @param base 一元表达式
     * @param left 待分析的一元表达式是否为真正即将被赋值的左值
     * @return 表达式结果对应的符号, 左值错误则返回立即数 0
     */
    public Operand analyseBasePrimaryExp(BasePrimaryExp base, boolean left) {
        if (base instanceof SubExp) {
            SubExp sub = (SubExp) base;
            if (!((SubExp) base).hasRightParenthesis()) {
                errorTable.add(new Error(Error.Type.MISSING_RIGHT_PARENT, sub.getLeftParenthesis().lineNumber()));
            }
            return analyseExp(sub.getExp());
        } else if (base instanceof LVal) {
            // 符号表相关错误(变量未定义等)
            LVal val = (LVal) base;
            if (!currentSymTable.contains(val.getName().getName(), true)) {
                errorTable.add(new Error(Error.Type.UNDEFINED_IDENT, val.getName().lineNumber()));
                return new Immediate(0);
            }
            Symbol symbol = currentSymTable.get(val.getName().getName(), true);
            // 缺中括号错误
            Iterator<LVal.Index> iterIndex = val.iterIndexes();
            List<Operand> indexes = new ArrayList<>();
            while (iterIndex.hasNext()) {
                if (symbol.getRefType().equals(Symbol.RefType.ITEM)) {
                    throw new AssertionError("int symbol has index");
                }
                LVal.Index index = iterIndex.next();
                if (!index.hasRightBracket()) {
                    errorTable.add(new Error(Error.Type.MISSING_RIGHT_BRACKET, index.getLeftBracket().lineNumber()));
                    return new Immediate(0);
                }
                indexes.add(analyseExp(index.getIndex()));
            }
            if ((!indexes.isEmpty() && symbol.getRefType().equals(Symbol.RefType.ITEM))
                    || ( symbol.getRefType().equals(Symbol.RefType.ARRAY) && indexes.size() > symbol.getDimCount())
                    || (symbol.getRefType().equals(Symbol.RefType.POINTER) && indexes.size() > symbol.getDimCount() + 1)) {
                throw new AssertionError("Array indexes more than dimension!");
            }
            Operand offset = new Immediate(0);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                // offset += arrayIndexes[i] * baseOffset;
                Symbol prod = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
                Operand offsetBase = new Immediate(symbol.getBaseOfDim(i));
                currentBlock.append(new BinaryOp(BinaryOp.Op.MUL, indexes.get(i), offsetBase, prod));
                Symbol sum = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
                currentBlock.append(new BinaryOp(BinaryOp.Op.ADD, offset, prod, sum));
                offset = sum;
            }
            if (symbol.getRefType().equals(Symbol.RefType.ITEM)) {
                return symbol;
            } else if (symbol.getRefType().equals(Symbol.RefType.ARRAY)) {
                // ARRAY
                int depth = indexes.size();
                Symbol ptr = symbol.toPointer().subPointer(depth);
                currentBlock.append(new AddressOffset(symbol, offset, ptr));
                if (left || depth < symbol.getDimCount()) {
                    return ptr;
                } else {
                    Symbol value = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
                    currentBlock.append(new PointerOp(PointerOp.Op.LOAD, ptr, value));
                    return value;
                }
            } else {
                // POINTER
                int depth = indexes.size();
                Symbol ptr = symbol.subPointer(depth);
                currentBlock.append(new AddressOffset(symbol, offset, ptr));
                if (left || depth <= symbol.getDimCount()) {
                    return ptr;
                } else {
                    Symbol value = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.ITEM);
                    currentBlock.append(new PointerOp(PointerOp.Op.LOAD, ptr, value));
                    return value;
                }
            }
        } else if (base instanceof Number) {
            return new Immediate(((Number) base).getValue().getValue());
        } else {
            throw new AssertionError("BasePrimaryExp refType error!");
        }
    }

    /**
     * 语句和块的分析
     */
    /* ---- 简单语句 ---- */
    // 简单语句只会生成基本的中间代码（四元式条目），只需追加到当前的块中即可

    private Symbol checkLVal(LVal left) {
        Operand ln = analyseBasePrimaryExp(left, true);
        if (Objects.isNull(ln) || ln instanceof Immediate) {
            return null;
        }
        if (!(ln instanceof Symbol)) {
            return null; // due to undefined symbol error
        }
        Symbol leftSym = (Symbol) ln;
        if (leftSym.isConstant()) {
            errorTable.add(new Error(Error.Type.MODIFY_CONST, left.getName().lineNumber()));
            return null;
        }
        return leftSym;
    }

    public void analyseAssignStmt(AssignStmt stmt) {
        // 常量修改错误
        LVal left = stmt.getLeftVal();
        Exp right = stmt.getExp();
        Symbol leftSym = checkLVal(left);
        Operand rn = analyseExp(right);
        if (Objects.isNull(rn)) {
            throw new AssertionError("Assign void to LVal");
        }
        if (Objects.isNull(leftSym)) {
            return;
        }
        assert !leftSym.getRefType().equals(Symbol.RefType.ARRAY);
        if (leftSym.getRefType().equals(Symbol.RefType.POINTER)) {
            currentBlock.append(new PointerOp(PointerOp.Op.STORE, leftSym, rn));
        } else {
            currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, rn, leftSym));
        }
    }

    public void analyseExpStmt(ExpStmt stmt) {
        // 这个最容易
        analyseExp(stmt.getExp());
    }

    public void analyseInputStmt(InputStmt stmt) {
        LVal left = stmt.getLeftVal();
        // 检查符号表，检查变量类型
        Symbol leftSym = checkLVal(left);
        currentBlock.append(new Input(leftSym));
    }

    /**
     * 对格式字符串进行检查
     * @param format 格式字符串
     * @return -1 if 格式字符串格式错误，>= 0 if 格式字符串合法，返回 "%d" 的个数
     */
    private int checkFormatString(String format) {
        int l = format.length();
        int count = 0;
        for (int i = 0; i < l; i++) {
            char c = format.charAt(i);
            if (c != 32 && c != 33 && !(c >= 40 && c <= 126)) {
                if (c == '%') {
                    if (i < l - 1 && format.charAt(i + 1) == 'd') {
                        count = count + 1;
                        continue;
                    } else {
                        return -1;
                    }
                }
                return -1;
            }
            if (c == 92 && (i >= l - 1 || format.charAt(i + 1) != 'n')) {
                return -1;
            }
        }
        return count;
    }

    public void analyseOutputStmt(OutputStmt stmt) {
        // 检查 FormatString, 检查参数和格式符的个数(以及类型)
        // 生成输出语句
        String format = stmt.getFormatString().getInner();
        int count = checkFormatString(format);
        if (count < 0) {
            errorTable.add(new Error(Error.Type.ILLEGAL_CHAR, stmt.getFormatString().lineNumber()));
            return;
        }
        List<Operand> params = new ArrayList<>();
        Iterator<Exp> iter = stmt.iterParameters();
        while (iter.hasNext()) {
            Exp exp = iter.next();
            Operand param = analyseExp(exp);
            params.add(param);
        }
        if (params.size() != count) {
            errorTable.add(new Error(Error.Type.MISMATCH_PRINTF, stmt.getFormatString().lineNumber()));
            return;
        }
        currentBlock.append(new PrintFormat(format, params));
    }

    public void analyseReturnStmt(ReturnStmt stmt) {
        // return 语句类型和当前函数的类型是不是匹配
        if (Objects.isNull(currentFunc)) {
            throw new AssertionError("Return in no function!");
        }
        if (currentFunc.getReturnType().equals(FuncMeta.ReturnType.INT)) {
            if (stmt.hasValue()) {
                Operand value = analyseExp(stmt.getValue());
                currentBlock.append(new Return(value));
            } else {
                // 有返回值的函数存在 return;
                assert false;
            }
        } else {
            if (stmt.hasValue()) {
                errorTable.add(new Error(Error.Type.RETURN_VALUE_VOID, stmt.getReturnTk().lineNumber()));
            } else {
                currentBlock.append(new Return());
            }
        }
    }

    public void analyseBreakStmt(BreakStmt stmt) {
        // 就是一个跳转，跳到往上的循环的下一层
        // 检查是否非循环块
        if (loopBlocks.empty()) {
            errorTable.add(new Error(Error.Type.CONTROL_OUTSIDE_LOOP, stmt.getBreakTk().lineNumber()));
            return;
        }
        BasicBlock follow = loopFollows.peek();
        currentBlock.append(new Jump(follow));
    }

    public void analyseContinueStmt(ContinueStmt stmt) {
        // 也是一个跳转，跳到往上的循环的头
        // 检查是否非循环块
        if (loopBlocks.empty()) {
            errorTable.add(new Error(Error.Type.CONTROL_OUTSIDE_LOOP, stmt.getContinueTk().lineNumber()));
            return;
        }
        BasicBlock loop = loopBlocks.peek();
        currentBlock.append(new Jump(loop));
    }

    /* ---- 复杂语句 ---- */
    // 这部分语句会产生新的基本块，以及更深嵌套的符号表
    public void analyseIfStmt(IfStmt stmt) throws ConstExpException {
        // 缺右括号
        if (!stmt.hasRightParenthesis()) {
            errorTable.add(new Error(Error.Type.MISSING_RIGHT_PARENT, stmt.getLeftParenthesis().lineNumber()));
        }
        // 生成新的基本块
        Operand cond = analyseCond(stmt.getCondition());
        BasicBlock current = currentBlock;
        BasicBlock follow = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock then = new BasicBlock("IF_THEN_" + newBlockCount(), BasicBlock.Type.BRANCH);
        if (stmt.hasElse()) {
            BasicBlock elseBlk = new BasicBlock("IF_ELSE_" + newBlockCount(), BasicBlock.Type.BRANCH);
            current.append(new BranchIfElse(cond, then, elseBlk));
            currentBlock = then;
            analyseStmt(stmt.getThenStmt());
            currentBlock.append(new Jump(follow));
            currentBlock = elseBlk;
            analyseStmt(stmt.getElseStmt());
        } else {
            current.append(new BranchIfElse(cond, then, follow));
            currentBlock = then;
            analyseStmt(stmt.getThenStmt());
        }
        currentBlock.append(new Jump(follow));
        currentBlock = follow;
    }

    public void analyseWhileStmt(WhileStmt stmt) throws ConstExpException {
        // 缺右括号
        if (!stmt.hasRightParenthesis()) {
            errorTable.add(new Error(Error.Type.MISSING_RIGHT_PARENT, stmt.getLeftParenthesis().lineNumber()));
        }
        // 生成新的基本块
        BasicBlock current = currentBlock;
        BasicBlock follow = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock body = new BasicBlock("LOOP_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock loop = new BasicBlock("WHILE_" + newBlockCount(), BasicBlock.Type.LOOP);
        current.append(new Jump(loop));
        loopBlocks.push(loop);
        loopFollows.push(follow);
        currentBlock = loop;
        Operand cond = analyseCond(stmt.getCondition());
        currentBlock.append(new BranchIfElse(cond, body, follow));
        currentBlock = body;
        analyseStmt(stmt.getStmt());
        loopFollows.pop();
        loopBlocks.pop();
        currentBlock.append(new Jump(loop));
        currentBlock = follow;
    }

    public BasicBlock analyseBlock(Block stmt) throws ConstExpException {
        BasicBlock block = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        if (Objects.nonNull(currentBlock)) {
            currentBlock.append(new Jump(block));
        }
        blockDepth++;
        currentBlock = block;
        BasicBlock follow = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        currentSymTable = new SymTable(block.getLabel(), currentSymTable);  // symbol push stack
        // 一条一条语句去遍历就行
        Iterator<BlockItem> items = stmt.iterItems();
        while (items.hasNext()) {
            BlockItem item = items.next();
            if (item instanceof Stmt) {
                analyseStmt((Stmt) item);
            } else if (item instanceof Decl) {
                analyseDecl((Decl) item);
            } else {
                throw new AssertionError("BlockItem wrong refType!");
            }
        }
        currentBlock.append(new Jump(follow));
        currentBlock = follow;
        currentSymTable = currentSymTable.getParent();  // symbol pop stack
        blockDepth--;
        return block;
    }

    public void analyseStmt(Stmt stmt) throws ConstExpException {
        // 缺分号
        if (stmt.isEmpty()) {
            return;
        }
        if (stmt.isSimple()) {
            // check semi
            if (!stmt.hasSemicolon()) {
                errorTable.add(new Error(Error.Type.MISSING_SEMICOLON, stmt.getSimpleStmt().lineNumber()));
            }
            SplStmt simple = stmt.getSimpleStmt();
            if (simple instanceof AssignStmt) {
                analyseAssignStmt((AssignStmt) simple);
            } else if (simple instanceof BreakStmt) {
                analyseBreakStmt((BreakStmt) simple);
            } else if (simple instanceof ContinueStmt) {
                analyseContinueStmt((ContinueStmt) simple);
            } else if (simple instanceof ExpStmt) {
                analyseExpStmt((ExpStmt) simple);
            } else if (simple instanceof InputStmt) {
                analyseInputStmt((InputStmt) simple);
            } else if (simple instanceof OutputStmt) {
                analyseOutputStmt((OutputStmt) simple);
            } else if (simple instanceof ReturnStmt) {
                analyseReturnStmt((ReturnStmt) simple);
            } else {
                throw new AssertionError("SplStmt wrong refType!");
            }
        } else {
            CplStmt complex = stmt.getComplexStmt();
            if (complex instanceof IfStmt) {
                analyseIfStmt((IfStmt) complex);
            } else if (complex instanceof WhileStmt) {
                analyseWhileStmt((WhileStmt) complex);
            } else if (complex instanceof Block) {
                analyseBlock((Block) complex);
            } else {
                throw new AssertionError("CplStmt wrong refType!");
            }
        }
    }

    /**
     * 变量声明定义
     */
    public void analyseDecl(Decl decl) throws ConstExpException {
        // 检查分号
        if (!decl.hasSemicolon()) {
            errorTable.add(new Error(Error.Type.MISSING_SEMICOLON, decl.getBType().lineNumber()));
        }
        Def first = decl.getFirst();
        analyseDef(first);
        Iterator<Def> iter = decl.iterFollows();
        while (iter.hasNext()) {
            Def def = iter.next();
            analyseDef(def);
        }
    }

    private List<Exp> initFlattenHelper(ArrInitVal init) {
        List<Exp> inits = new ArrayList<>();
        if (init.hasFirst()) {
            InitVal first = init.getFirst();
            if (first instanceof ExpInitVal) {
                inits.add(((ExpInitVal) first).getExp());
            } else {
                inits.addAll(initFlattenHelper((ArrInitVal) first));
            }
            Iterator<InitVal> iter = init.iterFollows();
            while (iter.hasNext()) {
                InitVal v = iter.next();
                if (v instanceof ExpInitVal) {
                    inits.add(((ExpInitVal) v).getExp());
                } else {
                    inits.addAll(initFlattenHelper((ArrInitVal) v));
                }
            }
        }
        return inits;
    }

    public void analyseDef(Def def) throws ConstExpException {
        // 维护符号表，重定义错误
        Ident ident = def.getName();
        String name = ident.getName();
        List<Integer> arrayDims = new ArrayList<>();
        boolean constant = def.isConst();
        if (currentSymTable.contains(name, false)) {
            errorTable.add(new Error(Error.Type.DUPLICATED_IDENT, ident.lineNumber()));
            return;
        }
        // fix: 位于函数的顶层并且和形参相同也算重定义
        if (blockDepth == 1 && Objects.nonNull(currentFunc) && currentFunc.getParamTable().contains(name, false)) {
            errorTable.add(new Error(Error.Type.DUPLICATED_IDENT, ident.lineNumber()));
            return;
        }
        // (?): 和函数名相同不算重复定义
        if (!def.isArray()) {
            if (def.isInitialized()) {
                ExpInitVal init = (ExpInitVal) def.getInitVal();
                if (init.isConst()) {
                    int value = 0;
                    try {
                        value = new CalcUtil(currentSymTable, errorTable).calcExp(init.getExp());
                    } catch (VarAtConstException e) {
                        errorTable.add(new Error(Error.Type.VAR_AT_CONST, e.getLineNumber()));
                    }
                    Symbol sym = new Symbol(name, Symbol.BasicType.INT, constant, value);
                    if (Objects.nonNull(currentFunc)) {
                        stackSize += sym.capacity();
                        sym.setAddress(stackSize);
                        currentFunc.updateStackSize(stackSize);
                        sym.setLocal(true);
                        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, new Immediate(value), sym));
                    } else {
                        sym.setAddress(currentSymTable.capacity());
                        middleCode.addGlobalVariable(sym.getName(), sym.getInitValue(), sym.getAddress());
                    }
                    currentSymTable.add(sym);
                } else {
                    if (Objects.isNull(currentFunc)) { // 没有在函数里，则必须能编译期算出
                        int value = 0;
                        try {
                            value = new CalcUtil(currentSymTable, errorTable).calcExp(init.getExp());
                        } catch (VarAtConstException e) {
                            errorTable.add(new Error(Error.Type.VAR_AT_CONST, e.getLineNumber()));
                        }
                        Symbol sym = new Symbol(name, Symbol.BasicType.INT, constant, value);
                        sym.setAddress(currentSymTable.capacity());
                        currentSymTable.add(sym);
                        middleCode.addGlobalVariable(sym.getName(), sym.getInitValue(), sym.getAddress());
                    } else {    // 在函数里的非常量，可以运行时计算
                        Symbol sym = new Symbol(name, Symbol.BasicType.INT);
                        stackSize += sym.capacity();
                        sym.setAddress(stackSize);
                        currentFunc.updateStackSize(stackSize);
                        sym.setLocal(true);
                        currentSymTable.add(sym);
                        Operand val = analyseExp(init.getExp());
                        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, val, sym));
                    }
                }
            } else {
                Symbol sym;
                if (Objects.isNull(currentFunc)) {
                    sym = new Symbol(name, Symbol.BasicType.INT, false, 0);
                    sym.setAddress(currentSymTable.capacity());
                    middleCode.addGlobalVariable(sym.getName(), sym.getInitValue(), sym.getAddress());
                } else {
                    sym = new Symbol(name, Symbol.BasicType.INT);
                    stackSize += sym.capacity();
                    sym.setAddress(stackSize);
                    currentFunc.updateStackSize(stackSize);
                    sym.setLocal(true);
                }
                currentSymTable.add(sym);
            }
        } else {
            // 加载一下维数和每个维度的长度
            Iterator<Def.ArrDef> iter = def.iterArrDefs();
            int totalSize = 1;
            while (iter.hasNext()) {
                Def.ArrDef ad = iter.next();
                if (!ad.hasRightBracket()) {
                    errorTable.add(new Error(Error.Type.MISSING_RIGHT_BRACKET, ident.lineNumber()));
                }
                int value = 0;
                try {
                    value = new CalcUtil(currentSymTable, errorTable).calcExp(ad.getArrLength());
                } catch (VarAtConstException e) {
                    errorTable.add(new Error(Error.Type.VAR_AT_CONST, e.getLineNumber()));
                }
                totalSize *= value;
                arrayDims.add(value);
            }
            if (def.isInitialized()) {
                ArrInitVal init = (ArrInitVal) def.getInitVal();
                List<Exp> initExps = initFlattenHelper(init);
                if (init.isConst() || Objects.isNull(currentFunc)) { // 常量或者位于全局
                    List<Integer> initValues = new ArrayList<>();
                    for (Exp exp : initExps) {
                        int value = 0;
                        try {
                            value = new CalcUtil(currentSymTable, errorTable).calcExp(exp);
                        } catch (VarAtConstException e) {
                            errorTable.add(new Error(Error.Type.VAR_AT_CONST, e.getLineNumber()));
                        }
                        initValues.add(value);
                    }
                    Symbol sym = new Symbol(name, Symbol.BasicType.INT, arrayDims, constant, initValues);
                    if (Objects.nonNull(currentFunc)) {
                        stackSize += sym.capacity();
                        sym.setAddress(stackSize);
                        currentFunc.updateStackSize(stackSize);
                        sym.setLocal(true);
                        // 初始化
                        int offset = 0;
                        for (int val : initValues) {
                            Symbol ptr = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.POINTER);
                            currentBlock.append(new AddressOffset(sym, new Immediate(offset * Symbol.SIZEOF_INT), ptr));
                            currentBlock.append(new PointerOp(PointerOp.Op.STORE, ptr, new Immediate(val)));
                            offset++;
                        }
                    } else {
                        sym.setAddress(currentSymTable.capacity());
                        middleCode.addGlobalArray(sym.getName(), sym.getInitArray(), sym.getAddress());
                    }
                    currentSymTable.add(sym);
                } else {
                    // 运行时赋值
                    Symbol sym = new Symbol(name, Symbol.BasicType.INT, arrayDims);
                    stackSize += sym.capacity();
                    sym.setAddress(stackSize);
                    currentFunc.updateStackSize(stackSize);
                    sym.setLocal(true);
                    currentSymTable.add(sym);
                    int offset = 0;
                    for (Exp exp: initExps) {
                        Operand op = analyseExp(exp);
                        Symbol ptr = Symbol.temporary(Symbol.BasicType.INT, Symbol.RefType.POINTER);
                        currentBlock.append(new AddressOffset(sym, new Immediate(offset * Symbol.SIZEOF_INT), ptr));
                        currentBlock.append(new PointerOp(PointerOp.Op.STORE, ptr, op));
                        offset++;
                    }
                }
            } else {
                Symbol sym;
                if (Objects.isNull(currentFunc)) {
                    List<Integer> initZeros = new ArrayList<>();
                    for (int i = 0; i < totalSize; i++) {
                        initZeros.add(0);
                    }
                    sym = new Symbol(name, Symbol.BasicType.INT, arrayDims, false, initZeros);
                    sym.setAddress(currentSymTable.capacity());
                    middleCode.addGlobalArray(sym.getName(), sym.getInitArray(), sym.getAddress());
                } else {
                    sym = new Symbol(name, Symbol.BasicType.INT, arrayDims);
                    stackSize += sym.capacity();
                    sym.setAddress(stackSize);
                    currentFunc.updateStackSize(stackSize);
                    sym.setLocal(true);
                }
                currentSymTable.add(sym);
            }
        }
    }

    /**
     * 函数与编译单元
     */
    private void funcFParamHelper(FuncFParam param, FuncMeta meta) throws ConstExpException {
        String argName = param.getName().getName();
        Symbol arg;
        // 形参不能重名
        if (meta.getParamTable().contains(argName, false)) {
            errorTable.add(new Error(Error.Type.DUPLICATED_IDENT, param.getName().lineNumber()));
            return;
        }
        if (!param.isArray()) {
            arg = new Symbol(param.getName().getName(), Symbol.BasicType.INT);
        } else {
            List<Integer> dimSizes = new ArrayList<>();
            // first dim is ignored because Array-FParam is Pointer
            FuncFParam.FirstDim first = param.getFirstDim();
            if (!first.hasRightBracket()) {
                errorTable.add(new Error(Error.Type.MISSING_RIGHT_BRACKET, first.getLeftBracket().lineNumber()));
            }
            Iterator<FuncFParam.ArrayDim> iter = param.iterFollowDims();
            while (iter.hasNext()) {
                FuncFParam.ArrayDim dim = iter.next();
                if (!dim.hasRightBracket()) {
                    errorTable.add(new Error(Error.Type.MISSING_RIGHT_BRACKET, dim.getLeftBracket().lineNumber()));
                }
                ConstExp len = dim.getLength();
                int length = 0;
                try {
                    length = new CalcUtil(currentSymTable, errorTable).calcExp(len);
                } catch (VarAtConstException e) {
                    errorTable.add(new Error(Error.Type.VAR_AT_CONST, e.getLineNumber()));
                }
                dimSizes.add(length);
            }
            arg = new Symbol(argName, Symbol.BasicType.INT, dimSizes, false);
        }
        meta.addParam(arg);
        arg.setAddress(meta.getParamTable().capacity());
        arg.setLocal(true);
    }

    public void analyseFunc(FuncDef func) throws ConstExpException {
        // 缺右括号
        if (!func.hasRightParenthesis()) {
            errorTable.add(new Error(Error.Type.MISSING_RIGHT_PARENT, func.getName().lineNumber()));
        }
        // 维护函数符号表
        FuncMeta.ReturnType returnType = func.getType().getType().getType().equals(Token.Type.VOIDTK) ? FuncMeta.ReturnType.VOID : FuncMeta.ReturnType.INT;
        String name = func.getName().getName();
        if (middleCode.getFunctions().containsKey(name)) {
            errorTable.add(new Error(Error.Type.DUPLICATED_IDENT, func.getName().lineNumber()));
            return;
        }
        if (currentSymTable.contains(name, false)) {
            errorTable.add(new Error(Error.Type.DUPLICATED_IDENT, func.getName().lineNumber()));
            return;
        }
        FuncMeta meta = new FuncMeta(name, returnType, currentSymTable);
        middleCode.putFunction(meta);
        currentFunc = meta;
        // 遍历形参表
        if (func.hasFParams()) {
            FuncFParams fParams = func.getFParams();
            FuncFParam first = fParams.getFirst();
            funcFParamHelper(first, meta);
            Iterator<FuncFParam> iter = fParams.iterFollows();
            while (iter.hasNext()) {
                FuncFParam param = iter.next();
                funcFParamHelper(param, meta);
            }
        }
        // 处理函数体
        funcBodyHelper(func.getBody(), meta);
    }

    private void funcBodyHelper(Block funcBody, FuncMeta meta) throws ConstExpException {
        currentSymTable = meta.getParamTable();
        stackSize = meta.getParamTable().capacity();
        BasicBlock block = analyseBlock(funcBody);
        BasicBlock body = new BasicBlock(meta.getLabelName(), BasicBlock.Type.FUNC);
        body.append(new Jump(block));
        currentBlock.append(new Return());
        currentBlock = null;
        meta.loadBody(body);
        currentSymTable = currentSymTable.getParent();
        currentFunc = null;
        // Block funcBody = def.getBody();
        Iterator<BlockItem> iterItem = funcBody.iterItems();
        boolean returnFlag = false;
        while (iterItem.hasNext()) {
            BlockItem item = iterItem.next();
            if (!iterItem.hasNext()) {
                // last statement
                if (item instanceof Stmt && ((Stmt) item).isSimple() && ((Stmt) item).getSimpleStmt() instanceof ReturnStmt) {
                    returnFlag = true;
                }
            }
        }
        if (!(returnFlag || meta.getReturnType().equals(FuncMeta.ReturnType.VOID))) {
            errorTable.add(new Error(Error.Type.MISSING_RETURN, funcBody.getRightBrace().lineNumber()));
        }
    }

    /**
     * 最终顶层编译单元
     */
    public void analyseCompUnit(CompUnit unit) throws ConstExpException {
        Iterator<Decl> iterVars = unit.iterGlobalVars();
        while (iterVars.hasNext()) {
            Decl decl = iterVars.next();
            analyseDecl(decl);
        }
        Iterator<FuncDef> iterFunc = unit.iterFunctions();
        while (iterFunc.hasNext()) {
            FuncDef fun = iterFunc.next();
            analyseFunc(fun);
        }
        FuncMeta mainMeta = new FuncMeta(currentSymTable);
        currentFunc = mainMeta;
        middleCode.putFunction(mainMeta);
        MainFuncDef main = unit.getMainFunc();
        funcBodyHelper(main.getBody(), mainMeta);
        middleCode.setMainFunction(mainMeta);
    }
}
