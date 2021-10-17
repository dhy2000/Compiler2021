package intermediate.operand;

/**
 * 中间代码中的符号
 */
public class Symbol implements Operand {
    private final String name;
    private final String field;
    // private final String version; // SSA

    public enum Type {
        INT,
        ARRAY, // 到中间代码层面，数组只有一维了
    }

    private final Type type;

    public Symbol(String name, String field) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
    }

    public Symbol(String name, String field, Type type) {
        this.name = name;
        this.field = field;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return field + "_" + name + "[:" + type.name() + "]";
    }

    // Temporary Symbols
    private static int count = 0;

    public static Symbol temporary() {
        count = count + 1;
        return new Symbol(Integer.toString(count), "@TEMP");
    }
}
