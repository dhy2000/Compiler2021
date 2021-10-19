package intermediate.code;


import intermediate.symbol.Symbol;

/**
 * 变量的压栈出栈操作
 */
public class StackOp {
    public enum Op {
        PUSH,   // 定义局部变量（将局部变量分配至内存）
        POP     // 释放局部变量
    }

    private final Op op;
    private final Symbol symbol;

    public StackOp(Op op, Symbol symbol) {
        this.op = op;
        this.symbol = symbol;
    }

    public Op getOp() {
        return op;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "STACK." + op.name() + " " + symbol;
    }
}
