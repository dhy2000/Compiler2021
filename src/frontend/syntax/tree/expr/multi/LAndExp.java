package frontend.syntax.tree.expr.multi;

import frontend.lexical.token.Token;

import java.util.List;

public class LAndExp extends MultiExp<EqExp> {
    public LAndExp(EqExp first, List<Token> operators, List<EqExp> operands) {
        super(first, operators, operands, "<LAndExp>");
    }
}
