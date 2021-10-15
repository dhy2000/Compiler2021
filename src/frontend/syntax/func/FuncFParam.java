package frontend.syntax.func;

import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.expr.multi.ConstExp;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FuncFParam implements Component {

    private final Token bType;
    private final Ident name;

    private final FirstDim firstDim;
    private final List<ArrayDim> followDims;

    public FuncFParam(Token bType, Ident name) {
        assert bType.getType().equals(Token.Type.INTTK);
        this.bType = bType;
        this.name = name;
        this.firstDim = null;
        this.followDims = Collections.emptyList();
    }

    public FuncFParam(Token bType, Ident name, Token leftBracket, Token rightBracket) {
        this.bType = bType;
        this.name = name;
        this.firstDim = new FirstDim(leftBracket, rightBracket);
        this.followDims = Collections.emptyList();
    }

    public FuncFParam(Token bType, Ident name, Token leftBracket, Token rightBracket, List<ArrayDim> followDims) {
        this.bType = bType;
        this.name = name;
        this.firstDim = new FirstDim(leftBracket, rightBracket);
        this.followDims = followDims;
    }

    public Token getBType() {
        return bType;
    }

    public Ident getName() {
        return name;
    }

    public FirstDim getFirstDim() {
        return firstDim;
    }

    public boolean isArray() {
        return Objects.nonNull(firstDim);
    }

    public boolean isMultiDimArray() {
        return Objects.nonNull(firstDim) && !followDims.isEmpty();
    }

    public int dimension() {
        if (isArray()) {
            if (isMultiDimArray()) {
                return followDims.size() + 1;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public Iterator<ArrayDim> iterFollowDims() {
        return followDims.listIterator();
    }

    @Override
    public void output(PrintStream ps) {
        bType.output(ps);
        name.output(ps);
        if (Objects.nonNull(firstDim)) {
            firstDim.output(ps);
        }
        followDims.forEach(arrayDim -> arrayDim.output(ps));
        ps.println("<FuncFParam>");
    }

    public static class FirstDim implements Component {
        private final Token leftBracket;
        private final Token rightBracket;

        public FirstDim(Token leftBracket, Token rightBracket) {
            assert leftBracket.getType().equals(Token.Type.LBRACK);
            assert Objects.isNull(rightBracket) || rightBracket.getType().equals(Token.Type.RBRACK);
            this.leftBracket = leftBracket;
            this.rightBracket = rightBracket;
        }

        public Token getLeftBracket() {
            return leftBracket;
        }

        public Token getRightBracket() {
            return rightBracket;
        }

        public boolean hasRightBracket() {
            return Objects.nonNull(rightBracket);
        }

        @Override
        public void output(PrintStream ps) {
            leftBracket.output(ps);
            if (hasRightBracket()) {
                rightBracket.output(ps);
            }
        }
    }

    public static class ArrayDim extends FirstDim implements Component {

        private final ConstExp length;

        public ArrayDim(Token leftBracket, Token rightBracket, ConstExp length) {
            super(leftBracket, rightBracket);
            this.length = length;
        }

        public ConstExp getLength() {
            return length;
        }

        @Override
        public void output(PrintStream ps) {
            getLeftBracket().output(ps);
            length.output(ps);
            if (hasRightBracket()) {
                getRightBracket().output(ps);
            }
        }
    }
}
