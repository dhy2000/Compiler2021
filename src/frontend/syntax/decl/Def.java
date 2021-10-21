package frontend.syntax.decl;

import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.Component;
import frontend.syntax.expr.multi.ConstExp;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Def implements Component, Constable {
    private final boolean constant;

    private final Ident name;
    private final List<ArrDef> arrDefs;

    private final Token assignTk;
    private final InitVal initVal;

    // Variable, not initialized
    public Def(Ident name, List<ArrDef> arrDefs) {
        this.constant = false;
        this.name = name;
        this.arrDefs = arrDefs;
        this.assignTk = null;
        this.initVal = null;
    }

    // Variable or Constant, Initialized
    public Def(boolean constant, Ident name, List<ArrDef> arrDefs, Token assignTk, InitVal initVal) {
        assert Objects.nonNull(assignTk) && assignTk.getType().equals(Token.Type.ASSIGN);
        this.constant = constant;
        this.name = name;
        this.arrDefs = arrDefs;
        this.assignTk = assignTk;
        this.initVal = initVal;
    }

    public Ident getName() {
        return name;
    }

    @Override
    public boolean isConst() {
        return constant;
    }

    public boolean isArray() {
        return !arrDefs.isEmpty();
    }

    public int arrayDimension() {
        return arrDefs.size();
    }

    public Iterator<ArrDef> iterArrDefs() {
        return arrDefs.listIterator();
    }

    public boolean isInitialized() {
        return Objects.nonNull(assignTk) && Objects.nonNull(initVal);
    }

    public Token getAssignTk() {
        return assignTk;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    private String nodeName() {
        if (constant) {
            return "<ConstDef>";
        } else {
            return "<VarDef>";
        }
    }

    @Override
    public void output(PrintStream ps) {
        name.output(ps);
        arrDefs.forEach(arrDef -> arrDef.output(ps));
        if (isInitialized()) {
            assignTk.output(ps);
            initVal.output(ps);
        }
        ps.println(nodeName());
    }

    public static class ArrDef implements Component {
        private final Token leftBracket;
        private final Token rightBracket;
        private final ConstExp arrLength;

        public ArrDef(Token leftBracket, Token rightBracket, ConstExp arrLength) {
            this.leftBracket = leftBracket;
            this.rightBracket = rightBracket;
            this.arrLength = arrLength;
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

        public ConstExp getArrLength() {
            return arrLength;
        }

        @Override
        public void output(PrintStream ps) {
            leftBracket.output(ps);
            arrLength.output(ps);
            if (hasRightBracket()) {
                rightBracket.output(ps);
            }
        }
    }
}
