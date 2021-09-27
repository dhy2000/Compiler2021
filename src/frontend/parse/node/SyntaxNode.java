package frontend.parse.node;

import java.util.Iterator;
import java.util.List;

/**
 * 抽象的语法树节点类
 */
public abstract class SyntaxNode implements Iterable<SyntaxNode> {

    private final SyntaxNode parent;

    public SyntaxNode(SyntaxNode parent) {
        this.parent = parent;
    }

    protected abstract List<SyntaxNode> getChildren();

    public SyntaxNode getParent() {
        return parent;
    }

    public String getNodeType() {
        return getClass().getSimpleName();
    }

    @Override
    public Iterator<SyntaxNode> iterator() {
        return getChildren().listIterator();
    }
}
