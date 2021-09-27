package frontend.syntax.node.unit;

public enum FuncType {
    VOID("void"),
    INT("int")
    ;

    private final String value;

    FuncType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
