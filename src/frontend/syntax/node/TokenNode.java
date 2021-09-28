package frontend.syntax.node;

import frontend.lexical.token.Token;

/**
 * 只有一个单词的语法树节点 (终结符号)
 */
public class TokenNode extends SyntaxNode {
    private final Token token;

    public TokenNode(SyntaxNode parent, Token token) {
        super(parent);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String getNodeType() {
        return token.typeName() + " " + token.getContent();
    }

    @Override
    public void display() {
        System.out.println(getNodeType());
    }
}
