package frontend.syntax.tree.expr.multi;

import frontend.lexical.token.Token;

import java.util.List;

public class LOrExp extends MultiExp<LAndExp> {
    public LOrExp(LAndExp first, List<Token> operators, List<LAndExp> operands) {
        super(first, operators, operands, "<LOrExp>");
    }
}
