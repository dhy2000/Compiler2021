package intermediate.symbol;

import intermediate.operand.Operand;

import java.util.Collections;
import java.util.List;

/**
 * 符号表中的符号(表项)
 */
public class Symbol implements Operand {
    private final String name;
    private final String field;

    public enum Type {
        INT,
        ARRAY,      // 数组（定义的）
        POINTER,    // 数组的首地址(函数参数), 在符号表中第一维的长度随意
    }

    private final Type type;
    private int offset = -1; // Offset to $sp, -1 表示暂时未分配栈上空间

    private final boolean constant;
    private final List<Integer> dimSize;

    private final Integer initValue;        // 整数变量的初值
    private final List<Integer> initArray;  // 数组的初值（展平了的）

    public Symbol(String name, String field) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
        this.constant = false;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
    }

    public Symbol(String name, String field, boolean constant, int init) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
        this.constant = constant;
        this.initValue = init;
        this.dimSize = Collections.emptyList();
        this.initArray = Collections.emptyList();
    }

    public Symbol(String name, String field, List<Integer> dimSize) {
        this.name = name;
        this.field = field;
        this.type = Type.ARRAY;
        this.constant = false;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.emptyList();
    }

    public Symbol(String name, String field, List<Integer> dimSize, boolean constant, List<Integer> init) {
        this.name = name;
        this.field = field;
        this.type = Type.ARRAY;
        this.constant = constant;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.unmodifiableList(init);
    }

    public Symbol(String name, String field, List<Integer> dimSize, int nothing) {
        this.name = name;
        this.field = field;
        this.type = Type.POINTER;
        this.constant = false;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initValue = null;
        this.initArray = Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getField() {
        return field;
    }

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isConstant() {
        return constant;
    }

    public List<Integer> getDimSize() {
        return dimSize;
    }

    public Integer getInitValue() {
        return initValue;
    }

    public List<Integer> getInitArray() {
        return initArray;
    }

    @Override
    public String toString() {
        return name + "[sp+" + offset + "]:" + type;
    }

    // 临时变量暂时不具备栈上空间
    // TODO: 临时变量是在这里分配还是在基本块里分配？
    private static int tempCount = 0;
    public static Symbol temporary(String field) {
        tempCount = tempCount + 1;
        return new Symbol("tmp_" + tempCount, field);
    }
}
