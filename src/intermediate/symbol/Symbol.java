package intermediate.symbol;

import intermediate.operand.Operand;

import java.util.Collections;
import java.util.List;

/**
 * 符号表中的符号(表项)
 */
public class Symbol implements Operand {
    private static final int SIZEOF_INT = 4;

    private final String name;
    private final String field;

    public enum Type {
        INT,
        ARRAY,      // 数组（定义的）
        POINTER,    // 数组的首地址(函数参数), 在符号表中第一维的长度随意
    }

    private final Type type;
    private boolean local; // 是否为局部变量，如果是则基地址为当前运行栈栈底；如果否则基地址为全局空间头部
    private int address = -1; // 相对基地址的位移

    private final boolean constant;
    private final List<Integer> dimSize;    // 如果是指针则第一维没用

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

    public Symbol(String name, String field, int nothing) {
        this.name = name;
        this.field = field;
        this.type = Type.POINTER;
        this.constant = false;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
    }


    public String getName() {
        return name;
    }

    public String getField() {
        return field;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public Type getType() {
        return type;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
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

    public int capacity() {
        if (type.equals(Type.INT)) {
            return SIZEOF_INT;
        } else if (type.equals(Type.POINTER)) {
            return SIZEOF_INT;
        } else {
            return SIZEOF_INT * dimSize.stream().reduce((i, i2) -> i * i2).orElse(1);
        }
    }

    @Override
    public String toString() {
        return name + "[sp+" + address + "]:" + type;
    }

    // 临时变量暂时不具备栈上空间
    private static int tempCount = 0;
    public static Symbol temporary(String field, Type type) {
        assert type.equals(Type.INT) || type.equals(Type.POINTER);
        tempCount = tempCount + 1;
        if (type.equals(Type.POINTER)) {
            return new Symbol("ptr_" + tempCount, field, 1);
        } else {
            return new Symbol("tmp_" + tempCount, field);
        }
    }
}
