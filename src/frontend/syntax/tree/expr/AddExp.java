package frontend.syntax.tree.expr;

import frontend.lexical.token.Token;

import java.util.List;

public class AddExp extends MultiExp<MulExp> {
    public AddExp(MulExp first, List<Token> operators, List<MulExp> operands) {
        super(first, operators, operands, "<AddExp>");
    }
}
