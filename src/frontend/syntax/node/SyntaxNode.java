package frontend.syntax.node;

/**
 * 抽象的语法树节点类
 */
public abstract class SyntaxNode {

    private final SyntaxNode parent;

    public SyntaxNode(SyntaxNode parent) {
        this.parent = parent;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public abstract String getNodeType();

    /**
     * 语法分析作业输出需要
     */
    public abstract void display();
}
