package frontend.syntax.tree.expr.multi;

import frontend.lexical.token.Token;
import frontend.syntax.tree.expr.unary.UnaryExp;

import java.util.List;

public class MulExp extends MultiExp<UnaryExp> {
    public MulExp(UnaryExp first, List<Token> operators, List<UnaryExp> operands) {
        super(first, operators, operands, "MulExp");
    }
}
