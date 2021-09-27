package frontend.parse.node;

import frontend.tokenize.token.Token;

import java.util.Collections;
import java.util.List;

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
    protected List<SyntaxNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getNodeType() {
        return token.typeName() + " " + token.getContent();
    }
}
