package frontend.syntax.node.unit;

public class FuncDef {
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
}
