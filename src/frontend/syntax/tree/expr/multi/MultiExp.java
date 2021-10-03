package frontend.syntax.tree.expr.multi;

import frontend.lexical.token.Token;
import frontend.syntax.tree.Component;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 抽象的多个子节点的表达式 (连续的从左到右结合的二元运算)
 * @param <T> 比当前类型低一层次 (运算符优先级高一级) 的节点类型
 */
public abstract class MultiExp<T extends Component> implements Component {
    private final T first;
    private final List<Token> operators;
    private final List<T> operands;

    private final String name;

    public MultiExp(T first, List<Token> operators, List<T> operands, String name) {
        assert Objects.nonNull(first);
        assert operators.size() == operands.size();
        this.first = first;
        this.operators = operators;
        this.operands = operands;
        this.name = name;
    }

    public T getFirst() {
        return first;
    }

    public Iterator<Token> iterOperator() {
        return operators.listIterator();
    }

    public Iterator<T> iterOperand() {
        return operands.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        // output the first node
        first.output(ps);
        ps.println(name);
        // iterate the following nodes
        Iterator<Token> iterOperator = iterOperator();
        Iterator<T> iterOperand = iterOperand();
        while (iterOperator().hasNext() && iterOperand().hasNext()) {
            Token operator = iterOperator.next();
            T operand = iterOperand.next();
            operator.output(ps);
            operand.output(ps);
            ps.println(name);
        }
    }
}
