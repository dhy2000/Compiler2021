package frontend.visitor;

import exception.ConstExpException;
import frontend.error.Error;
import frontend.error.ErrorTable;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.AddExp;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.multi.MulExp;
import frontend.syntax.expr.unary.*;
import frontend.syntax.expr.unary.Number;
import intermediate.symbol.SymTable;
import intermediate.symbol.Symbol;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 常值表达式计算工具类
 */
public class CalcUtil {

    private final SymTable symTable;
    private final ErrorTable errorTable;

    public CalcUtil(SymTable symTable, ErrorTable errorTable) {
        this.symTable = symTable;
        this.errorTable = errorTable;
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
        if (!symTable.contains(name, true)) {
            errorTable.add(new Error(Error.Type.UNDEFINED_IDENT, lVal.getName().lineNumber()));
            return 0;
        }
        Symbol symbol = symTable.get(name, true);
        if (!symbol.isConstant()) {
            throw new ConstExpException(ident.lineNumber(), ident.getName());
        }

        if (symbol.getType().equals(Symbol.Type.INT)) {
            return symbol.getInitValue();
        } else if (symbol.getType().equals(Symbol.Type.ARRAY)) {
            ArrayList<Integer> indexes = new ArrayList<>();
            Iterator<LVal.Index> iter = lVal.iterIndexes();
            while (iter.hasNext()) {
                indexes.add(calcExp(iter.next().getIndex()));
            }
            int base = 1;
            int offset = 0;
            for (int i = indexes.size() - 1; i >= 0; i--) {
                offset += indexes.get(i) * base;
                if (i > 0) {
                    base = base * symbol.getDimSize().get(i);
                }
            }
            return symbol.getInitArray().get(offset);
        } else {
            throw new ConstExpException(ident.lineNumber(), ident.getName());
        }
    }

    public int extractNumber(Number number) {
        return number.getValue().getValue();
    }

    public int calcSubExp(SubExp exp) throws ConstExpException {
        return calcExp(exp.getExp());
    }
}
