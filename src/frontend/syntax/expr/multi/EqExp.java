package frontend.syntax.expr.multi;

import frontend.lexical.token.Token;

import java.util.List;

public class EqExp extends MultiExp<RelExp> {
    public EqExp(RelExp first, List<Token> operators, List<RelExp> operands) {
        super(first, operators, operands, "<EqExp>");
    }
}
