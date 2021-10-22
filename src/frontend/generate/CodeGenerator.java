package frontend.generate;

import exception.ConstExpException;
import frontend.error.Error;
import frontend.error.ErrorTable;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.CompUnit;
import frontend.syntax.Component;
import frontend.syntax.decl.*;
import frontend.syntax.expr.multi.Cond;
import frontend.syntax.expr.multi.ConstExp;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MultiExp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;
import frontend.syntax.func.FuncDef;
import frontend.syntax.func.FuncFParam;
import frontend.syntax.func.FuncFParams;
import frontend.syntax.func.MainFuncDef;
import frontend.syntax.stmt.Stmt;
import frontend.syntax.stmt.complex.*;
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
 * 语义分析器：遍历语法树，维护符号表，进行错误处理，生成中间代码
 * 和语法分析类似的类递归下降结构
 *
 * 这里把每部分的分析器合到了一个大类中，因为要维护一个统一的栈（符号作用域）
 *
 * TODO: 全局变量分配地址，参数的地址表示
 */
public class CodeGenerator {

    private SymTable currentSymTable = SymTable.global();   // 栈式符号表

    private final Intermediate intermediate = new Intermediate(); // 最终生成的中间代码

    public Intermediate getIntermediate() {
        return intermediate;
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
        // TODO: 逻辑运算的短路求值
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
                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, ident.lineNumber()));
            }
            if (!intermediate.getFunctions().containsKey(name)) {
                ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, ident.lineNumber()));
                return new Immediate(0);
            }
            FuncMeta func = intermediate.getFunctions().get(name);
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
                ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_NUM, ident.lineNumber()));
                error = true;
            } else {
                Iterator<Operand> iterParam = params.listIterator();
                Iterator<Symbol> iterArg = args.listIterator();
                while (iterParam.hasNext() && iterArg.hasNext()) {
                    Operand param = iterParam.next();
                    Symbol arg = iterArg.next();
                    if (Objects.isNull(param)) {
                        ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                        error = true;
                        break;
                    }
                    else if (param instanceof Immediate) {
                        if (!arg.getType().equals(Symbol.Type.INT)) {
                            ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
                            error = true;
                            break;
                        }
                    }
                    else {
                        assert param instanceof Symbol;
                        if (!((Symbol) param).getType().equals(arg.getType())) {
                            ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PARAM_TYPE, ident.lineNumber()));
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
                    Symbol r = Symbol.temporary(currentField(), Symbol.Type.INT);
                    currentBlock.append(new Call(func, params, r));
                    return r;
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
            Symbol tmp = Symbol.temporary(currentField(), Symbol.Type.INT);
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
                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, sub.getLeftParenthesis().lineNumber()));
            }
            return analyseExp(sub.getExp());
        } else if (base instanceof LVal) {
            // 符号表相关错误(变量未定义等)
            LVal val = (LVal) base;
            if (!currentSymTable.contains(val.getName().getName(), true)) {
                ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, val.getName().lineNumber()));
                return new Immediate(0);
            }
            Symbol symbol = currentSymTable.get(val.getName().getName(), true);
            // 缺中括号错误
            Iterator<LVal.Index> iterIndex = val.iterIndexes();
            List<Operand> indexes = new ArrayList<>();
            while (iterIndex.hasNext()) {
                if (symbol.getType().equals(Symbol.Type.INT)) {
                    throw new AssertionError("int symbol has index");
                }
                LVal.Index index = iterIndex.next();
                if (!index.hasRightBracket()) {
                    ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, index.getLeftBracket().lineNumber()));
                    return new Immediate(0);
                }
                indexes.add(analyseExp(index.getIndex()));
            }
            if ((!indexes.isEmpty() && symbol.getType().equals(Symbol.Type.INT))
                    || ( symbol.getType().equals(Symbol.Type.ARRAY) && indexes.size() > symbol.getDimCount())
                    || (symbol.getType().equals(Symbol.Type.POINTER) && indexes.size() > symbol.getDimCount() + 1)) {
                throw new AssertionError("Array indexes more than dimension!");
            }
            Operand offset = new Immediate(0);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                // offset += arrayIndexes[i] * baseOffset;
                Symbol prod = Symbol.temporary(currentField(), Symbol.Type.INT);
                Operand offsetBase = new Immediate(symbol.getBaseOfDim(i));
                currentBlock.append(new BinaryOp(BinaryOp.Op.MUL, indexes.get(i), offsetBase, prod));
                Symbol sum = Symbol.temporary(currentField(), Symbol.Type.INT);
                currentBlock.append(new BinaryOp(BinaryOp.Op.ADD, offset, prod, sum));
                offset = sum;
            }
            if (symbol.getType().equals(Symbol.Type.INT)) {
                return symbol;
            } else if (symbol.getType().equals(Symbol.Type.ARRAY)) {
                // ARRAY
                int depth = indexes.size();
                Symbol ptr = symbol.toPointer().subPointer(depth);
                currentBlock.append(new AddressOffset(symbol, offset, ptr));
                if (left || depth < symbol.getDimCount()) {
                    return ptr;
                } else {
                    Symbol value = Symbol.temporary(currentField(), Symbol.Type.INT);
                    currentBlock.append(new PointerOp(PointerOp.Op.LOAD, ptr, value));
                    return value;
                }
            } else {
                // POINTER
                int depth = indexes.size();
                Symbol ptr = symbol.subPointer(depth);
                if (left || depth <= symbol.getDimCount()) {
                    return ptr;
                } else {
                    Symbol value = Symbol.temporary(currentField(), Symbol.Type.INT);
                    currentBlock.append(new PointerOp(PointerOp.Op.LOAD, ptr, value));
                    return value;
                }
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
            ErrorTable.getInstance().add(new Error(Error.Type.MODIFY_CONST, left.getName().lineNumber()));
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
        assert !leftSym.getType().equals(Symbol.Type.ARRAY);
        if (leftSym.getType().equals(Symbol.Type.POINTER)) {
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
            ErrorTable.getInstance().add(new Error(Error.Type.ILLEGAL_CHAR, stmt.getFormatString().lineNumber()));
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
            ErrorTable.getInstance().add(new Error(Error.Type.MISMATCH_PRINTF, stmt.getFormatString().lineNumber()));
            return;
        }
        String label = intermediate.addGlobalString(format); // TODO: use label instead of local string
        currentBlock.append(new Output(label, params));
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
                ErrorTable.getInstance().add(new Error(Error.Type.RETURN_VALUE_VOID, stmt.getReturnTk().lineNumber()));
            } else {
                currentBlock.append(new Return());
            }
        }
    }

    public void analyseBreakStmt(BreakStmt stmt) {
        // 就是一个跳转，跳到往上的循环的下一层
        // 检查是否非循环块
        if (loopBlocks.empty()) {
            ErrorTable.getInstance().add(new Error(Error.Type.CONTROL_OUTSIDE_LOOP, stmt.getBreakTk().lineNumber()));
            return;
        }
        BasicBlock follow = loopFollows.peek();
        currentBlock.append(new Jump(follow));
    }

    public void analyseContinueStmt(ContinueStmt stmt) {
        // 也是一个跳转，跳到往上的循环的头
        // 检查是否非循环块
        if (loopBlocks.empty()) {
            ErrorTable.getInstance().add(new Error(Error.Type.CONTROL_OUTSIDE_LOOP, stmt.getContinueTk().lineNumber()));
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
            ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, stmt.getLeftParenthesis().lineNumber()));
        }
        // 生成新的基本块
        Operand cond = analyseCond(stmt.getCondition());
        BasicBlock current = currentBlock;
        BasicBlock follow = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock then = new BasicBlock("IF_" + newBlockCount(), BasicBlock.Type.BRANCH);
        if (stmt.hasElse()) {
            BasicBlock elseBlk = new BasicBlock("IF_" + newBlockCount(), BasicBlock.Type.BRANCH);
            current.append(new BranchIfElse(cond, then, elseBlk));
            currentBlock = then;
            analyseStmt(stmt.getThenStmt());
            then.append(new Jump(follow));
            currentBlock = elseBlk;
            analyseStmt(stmt.getElseStmt());
            elseBlk.append(new Jump(follow));
        } else {
            current.append(new BranchIfElse(cond, then, follow));
            currentBlock = then;
            analyseStmt(stmt.getThenStmt());
            then.append(new Jump(follow));
        }
        currentBlock = follow;
    }

    public void analyseWhileStmt(WhileStmt stmt) throws ConstExpException {
        // 缺右括号
        if (!stmt.hasRightParenthesis()) {
            ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, stmt.getLeftParenthesis().lineNumber()));
        }
        // 生成新的基本块
        BasicBlock current = currentBlock;
        BasicBlock follow = new BasicBlock("B_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock body = new BasicBlock("BODY_" + newBlockCount(), BasicBlock.Type.BASIC);
        BasicBlock loop = new BasicBlock("WHILE_" + newBlockCount(), BasicBlock.Type.LOOP);
        current.append(new Jump(loop));
        loopBlocks.push(loop);
        loopFollows.push(follow);
        currentBlock = loop;
        Operand cond = analyseCond(stmt.getCondition());
        loop.append(new BranchIfElse(cond, body, follow));
        currentBlock = body;
        analyseStmt(stmt.getStmt());
        loopFollows.pop();
        loopBlocks.pop();
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
                throw new AssertionError("BlockItem wrong type!");
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
                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_SEMICOLON, stmt.getSimpleStmt().lineNumber()));
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
                throw new AssertionError("SplStmt wrong type!");
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
                throw new AssertionError("CplStmt wrong type!");
            }
        }
    }

    /**
     * 变量声明定义
     */
    public void analyseDecl(Decl decl) throws ConstExpException {
        // 检查分号
        if (!decl.hasSemicolon()) {
            ErrorTable.getInstance().add(new Error(Error.Type.MISSING_SEMICOLON, decl.getBType().lineNumber()));
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
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, ident.lineNumber()));
            return;
        }
        // fix: 位于函数的顶层并且和形参相同也算重定义
        if (blockDepth == 1 && Objects.nonNull(currentFunc) && currentFunc.getParamTable().contains(name, false)) {
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, ident.lineNumber()));
            return;
        }
        // 和函数名相同也算重复定义
        if (intermediate.getFunctions().containsKey(name)) {
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, ident.lineNumber()));
            return;
        }
        if (!def.isArray()) {
            if (def.isInitialized()) {
                ExpInitVal init = (ExpInitVal) def.getInitVal();
                if (init.isConst()) {
                    int value = new CalcUtil(currentSymTable).calcExp(init.getExp());
                    Symbol sym = new Symbol(name, currentField(), constant, value);
                    if (Objects.nonNull(currentFunc)) {
                        sym.setAddress(stackSize);
                        sym.setLocal(true);
                        stackSize += sym.capacity();
                        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, new Immediate(value), sym));
                    } else {
                        sym.setAddress(currentSymTable.capacity());
                        intermediate.addGlobalVariable(sym.getName(), sym.getInitValue());
                    }
                    currentSymTable.add(sym);
                } else {
                    if (Objects.isNull(currentFunc)) { // 没有在函数里，则必须能编译期算出
                        int value = new CalcUtil(currentSymTable).calcExp(init.getExp());
                        Symbol sym = new Symbol(name, currentField(), constant, value);
                        sym.setAddress(currentSymTable.capacity());
                        currentSymTable.add(sym);
                        intermediate.addGlobalVariable(sym.getName(), sym.getInitValue());
                    } else {    // 在函数里的非常量，可以运行时计算
                        Symbol sym = new Symbol(name, currentField());
                        sym.setAddress(stackSize);
                        sym.setLocal(true);
                        stackSize += sym.capacity();
                        currentSymTable.add(sym);
                        Operand val = analyseExp(init.getExp());
                        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, val, sym));
                    }
                }
            } else {
                Symbol sym;
                if (Objects.isNull(currentFunc)) {
                    sym = new Symbol(name, currentField(), false, 0);
                    sym.setAddress(currentSymTable.capacity());
                    intermediate.addGlobalVariable(sym.getName(), sym.getInitValue());
                } else {
                    sym = new Symbol(name, currentField());
                    sym.setAddress(stackSize);
                    sym.setLocal(true);
                    stackSize += sym.capacity();
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
                    ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, ident.lineNumber()));
                }
                int value = new CalcUtil(currentSymTable).calcExp(ad.getArrLength());
                totalSize *= value;
                arrayDims.add(value);
            }
            if (def.isInitialized()) {
                ArrInitVal init = (ArrInitVal) def.getInitVal();
                List<Exp> initExps = initFlattenHelper(init);
                if (init.isConst() || Objects.isNull(currentFunc)) { // 常量或者位于全局
                    List<Integer> initValues = new ArrayList<>();
                    for (Exp exp : initExps) {
                        int value = new CalcUtil(currentSymTable).calcExp(exp);
                        initValues.add(value);
                    }
                    Symbol sym = new Symbol(name, currentField(), arrayDims, constant, initValues);
                    if (Objects.nonNull(currentFunc)) {
                        sym.setAddress(stackSize);
                        sym.setLocal(true);
                        stackSize += sym.capacity();
                    } else {
                        sym.setAddress(currentSymTable.capacity());
                        intermediate.addGlobalArray(sym.getName(), sym.getInitArray());
                    }
                    currentSymTable.add(sym);
                } else {
                    // 运行时赋值
                    Symbol sym = new Symbol(name, currentField(), arrayDims);
                    sym.setAddress(stackSize);
                    sym.setLocal(true);
                    stackSize += sym.capacity();
                    currentSymTable.add(sym);
                    int offset = 0;
                    for (Exp exp: initExps) {
                        Operand op = analyseExp(exp);
                        Symbol ptr = new Symbol("ptr_" + newBlockCount(), currentField(), false);
                        currentBlock.append(new AddressOffset(sym, new Immediate(offset), ptr));
                        currentBlock.append(new UnaryOp(UnaryOp.Op.MOV, op, ptr));
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
                    sym = new Symbol(name, currentField(), arrayDims, false, initZeros);
                    sym.setAddress(currentSymTable.capacity());
                    intermediate.addGlobalArray(sym.getName(), sym.getInitArray());
                } else {
                    sym = new Symbol(name, currentField(), arrayDims);
                    sym.setAddress(stackSize);
                    sym.setLocal(true);
                    stackSize += sym.capacity();
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
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, param.getName().lineNumber()));
            return;
        }
        if (!param.isArray()) {
            arg = new Symbol(param.getName().getName(), meta.getParamTable().getField());
        } else {
            List<Integer> dimSizes = new ArrayList<>();
            // first dim is ignored because Array-FParam is Pointer
            FuncFParam.FirstDim first = param.getFirstDim();
            if (!first.hasRightBracket()) {
                ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, first.getLeftBracket().lineNumber()));
            }
            Iterator<FuncFParam.ArrayDim> iter = param.iterFollowDims();
            while (iter.hasNext()) {
                FuncFParam.ArrayDim dim = iter.next();
                if (!dim.hasRightBracket()) {
                    ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_BRACKET, dim.getLeftBracket().lineNumber()));
                }
                ConstExp len = dim.getLength();
                int length = new CalcUtil(currentSymTable).calcExp(len);
                dimSizes.add(length);
            }
            arg = new Symbol(argName, meta.getParamTable().getField(), dimSizes, false);
        }
        arg.setAddress(meta.getParamTable().capacity());
        arg.setLocal(true);
        meta.addParam(arg);
    }

    public void analyseFunc(FuncDef func) throws ConstExpException {
        // 缺右括号
        if (!func.hasRightParenthesis()) {
            ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RIGHT_PARENT, func.getName().lineNumber()));
        }
        // 维护函数符号表
        FuncMeta.ReturnType returnType = func.getType().getType().getType().equals(Token.Type.VOIDTK) ? FuncMeta.ReturnType.VOID : FuncMeta.ReturnType.INT;
        String name = func.getName().getName();
        if (intermediate.getFunctions().containsKey(name)) {
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, func.getName().lineNumber()));
            return;
        }
        if (currentSymTable.contains(name, false)) {
            ErrorTable.getInstance().add(new Error(Error.Type.DUPLICATED_IDENT, func.getName().lineNumber()));
            return;
        }
        FuncMeta meta = new FuncMeta(name, returnType, currentSymTable);
        intermediate.putFunction(meta);
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
        BasicBlock body = new BasicBlock(meta.getName(), BasicBlock.Type.FUNC);
        body.append(new Jump(block));
        currentBlock.append(new Return());
        currentBlock = block;
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
            ErrorTable.getInstance().add(new Error(Error.Type.MISSING_RETURN, funcBody.getRightBrace().lineNumber()));
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
        FuncMeta mainMeta = new FuncMeta("main", FuncMeta.ReturnType.INT, currentSymTable);
        currentFunc = mainMeta;
        intermediate.putFunction(mainMeta);
        MainFuncDef main = unit.getMainFunc();
        funcBodyHelper(main.getBody(), mainMeta);
    }
}
