package frontend.analyse;

import exception.ConstExpException;
import frontend.error.Error;
import frontend.error.ErrorTable;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.symbol.SymTable;
import frontend.symbol.type.ArrayType;
import frontend.symbol.type.BasicType;
import frontend.symbol.type.VarType;
import frontend.syntax.expr.multi.AddExp;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MulExp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 表达式工具类，支持计算常量表达式的值
 */
public class ExpUtil {

    private final SymTable sym;

    public ExpUtil(SymTable sym) {
        this.sym = sym;
    }

    public int calcExp(Exp exp) throws ConstExpException {
        return calcAddExp(exp.getAddExp());
    }

    public int calcAddExp(AddExp exp) throws ConstExpException {
        int result = calcMulExp(exp.getFirst());
        Iterator<Token> iterOperator = exp.iterOperator();
        Iterator<MulExp> iterOperand = exp.iterOperand();
        while (iterOperator.hasNext() && iterOperand.hasNext()) {
            Token op = iterOperator.next();
            MulExp mul = iterOperand.next();
            switch (op.getType()) {
                case PLUS: result = result + calcMulExp(mul); break;
                case MINU: result = result - calcMulExp(mul); break;
                default: assert false;
            }
        }
        return result;
    }

    public int calcMulExp(MulExp exp) throws ConstExpException {
        int result = calcUnaryExp(exp.getFirst());
        Iterator<Token> iterOperator = exp.iterOperator();
        Iterator<UnaryExp> iterOperand = exp.iterOperand();
        while (iterOperator.hasNext() && iterOperand.hasNext()) {
            Token op = iterOperator.next();
            UnaryExp unary = iterOperand.next();
            switch (op.getType()) {
                case MULT: result = result * calcUnaryExp(unary); break;
                case DIV: result = result / calcUnaryExp(unary); break;
                case MOD: result = result % calcUnaryExp(unary); break;
                default: assert false;
            }
        }
        return result;
    }

    public int calcUnaryExp(UnaryExp exp) throws ConstExpException {
        BaseUnaryExp base = exp.getBase();
        int result = 0;
        if (base instanceof FunctionCall) {
            FunctionCall call = (FunctionCall) base;
            throw new ConstExpException(call.getName().lineNumber(), call.getName().getName());
        } else if (base instanceof PrimaryExp) {
            BasePrimaryExp primary = ((PrimaryExp) base).getBase();
            if (primary instanceof SubExp) {
                result = calcSubExp((SubExp) primary);
            } else if (primary instanceof LVal) {
                result = calcLVal((LVal) primary);
            } else if (primary instanceof Number) {
                result = extractNumber((Number) primary);
            } else {
                assert false;
            }
        } else {
            assert false;
        }
        Iterator<Token> iterOp = exp.iterUnaryOp();
        while (iterOp.hasNext()) {
            Token op = iterOp.next();
            switch (op.getType()) {
                case PLUS: break;
                case MINU: result = -result; break;
                case NOT: result = (result == 0) ? 1 : 0; break;
                default: assert false;
            }
        }
        return result;
    }

    public int calcLVal(LVal lVal) throws ConstExpException {
        Ident ident = lVal.getName();
        String name = ident.getName();
        if (!sym.contains(name)) {
            ErrorTable.getInstance().add(new Error(Error.Type.UNDEFINED_IDENT, lVal.getName().lineNumber()));
            return 0;
        }
        SymTable.Item symbol = sym.getItemByName(name);
        if (!symbol.isInitialized() || symbol.isModified()) {
            throw new ConstExpException(ident.lineNumber(), ident.getName());
        }
        VarType symType = symbol.getType();
        if (symType instanceof BasicType) {
            return symbol.getInitValue();
        } else { // Array
            int dim = ((ArrayType) symType).getDimCount();
            if (dim > 2) {
                throw new AssertionError("More than 2 dim array");
            }
            ArrayList<Integer> arrayIndexes = new ArrayList<>();
            Iterator<LVal.Index> lValIndexes = lVal.iterIndexes();
            while (lValIndexes.hasNext()) {
                LVal.Index index = lValIndexes.next();
                arrayIndexes.add(calcExp(index.getIndex()));
            }
            int offsetBase = 1;
            int offset = 0;
            for (int i = arrayIndexes.size() - 1; i >= 0; i--) {
                offset += arrayIndexes.get(i) * offsetBase;
                if (i > 0) {
                    offsetBase = ((ArrayType) symType).getSizeOfInner(i - 1);
                }
            }
            return symbol.getInitArray().get(offset);
        }
    }

    public int extractNumber(Number number) {
        return number.getValue().getValue();
    }

    public int calcSubExp(SubExp exp) throws ConstExpException {
        return calcExp(exp.getExp());
    }
}
