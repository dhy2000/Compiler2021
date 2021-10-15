package frontend.error;

public class Error implements Comparable<Error> {

    private final Type type;
    private final int lineNum;

    public Error(Type type, int lineNum) {
        this.type = type;
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public Type getType() {
        return type;
    }

    public String getErrorTag() {
        return type.getTag();
    }

    @Override
    public String toString() {
        return lineNum + " " + getErrorTag();
    }

    @Override
    public int compareTo(Error o) {
        return Integer.compare(lineNum, o.getLineNum());
    }

    public enum Type {
        ILLEGAL_CHAR("a"),
        DUPLICATED_IDENT("b"),
        UNDEFINED_IDENT("c"),
        MISMATCH_PARAM_NUM("d"),
        MISMATCH_PARAM_TYPE("e"),
        RETURN_VALUE_VOID("f"),
        MISSING_RETURN("g"),
        MODIFY_CONST("h"),
        MISSING_SEMICOLON("i"),
        MISSING_RIGHT_PARENT("j"),
        MISSING_RIGHT_BRACKET("k"),
        MISMATCH_PRINTF("l"),
        CONTROL_OUTSIDE_LOOP("m"),
        ;

        private final String tag;

        Type(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
