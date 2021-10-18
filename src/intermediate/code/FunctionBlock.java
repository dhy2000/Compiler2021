package intermediate.code;

import intermediate.operand.Symbol;

import java.util.Collections;
import java.util.List;

public class FunctionBlock extends BasicBlock {

    private final List<Symbol> params; // 函数参数的符号表

    public FunctionBlock(String name, List<Symbol> params) {
        super(name);
        this.params = Collections.unmodifiableList(params);
    }

    public List<Symbol> getParams() {
        return params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[FUNCTION]" + getLabel() + ":\n");
        if (!params.isEmpty()) {
            sb.append("argv: ").append(params.stream().map(Symbol::toString).reduce((s, s2) -> s + ", " + s2)).append("\n");
        }
        ILinkNode node = getHead();
        while (node.hasNext()) {
            sb.append(node).append("\n");
            node = node.getNext();
        }
        return sb.toString();
    }
}
