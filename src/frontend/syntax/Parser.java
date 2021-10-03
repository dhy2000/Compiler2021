package frontend.syntax;

import frontend.lexical.TokenList;
import frontend.lexical.token.Token;

import java.util.ListIterator;

/**
 * 语法分析器, 采用递归下降方法进行分析
 */
public class Parser {
    /**
     * 存一个双向迭代器用于遍历单词列表
     */
    private final ListIterator<Token> iterator;

    /**
     * 构造方法, 根据词法分析的结果实例化语法分析器
     * @param tokens TokenList，源代码经词法分析拆分成单词的结果
     */
    public Parser(TokenList tokens) {
        this.iterator = tokens.listIterator();
    }

}
