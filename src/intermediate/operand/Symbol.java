package intermediate.operand;

import java.util.Objects;

/**
 * 中间代码中的符号
 */
public class Symbol implements Operand {
    private final String name;
    private final String field;

    private final Integer length; // Only ARRAY

    public enum Type {
        INT,
        ARRAY, // 到中间代码层面，数组只有一维了; 这里的 ARRAY 是个指针
    }

    private final Type type;

    public Symbol(String name, String field) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
        this.length = null;
    }

    public Symbol(String name, String field, Integer length) {
        this.name = name;
        this.field = field;
        this.type = Type.ARRAY;
        this.length = length;
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

    public Integer getLength() {
        return length;
    }

    public boolean hasLength() {
        return Objects.nonNull(length);
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
