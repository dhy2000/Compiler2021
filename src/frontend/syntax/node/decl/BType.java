package frontend.syntax.node.decl;

public enum BType {
    INT("int")
    ;

    private final String value;

    BType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
