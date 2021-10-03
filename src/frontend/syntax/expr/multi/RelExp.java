package frontend.syntax.expr.multi;

import frontend.lexical.token.Token;

import java.util.List;

public class RelExp extends MultiExp<AddExp> {
    public RelExp(AddExp first, List<Token> operators, List<AddExp> operands) {
        super(first, operators, operands, "<RelExp>");
    }
}
