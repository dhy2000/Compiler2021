package intermediate.symbol;

import intermediate.operand.Operand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 符号表中的符号(表项)
 */
public class Symbol implements Operand {
    public static final int SIZEOF_INT = 4;

    private final String name;
    private final String field;

    public enum Type {
        INT,
        ARRAY,      // 数组
        POINTER,    // 作为函数参数或者数组寻址后的临时变量，**比数组少一维**！
    }

    private final Type type;
    private boolean local; // 是否为局部变量，如果是则基地址为当前运行栈栈底；如果否则基地址为全局空间头部
    private int address = -1; // 相对基地址的位移

    private final boolean constant;
    private final List<Integer> dimSize;    // 每一维的长度，如果是指针则少一维
    private final List<Integer> dimBase;    // dimSize 的后缀积

    private final Integer initValue;        // 整数变量的初值
    private final List<Integer> initArray;  // 数组的初值（展平了的）

    private static List<Integer> suffixProduct(List<Integer> list, boolean pointer) {
        List<Integer> suffix = new ArrayList<>();
        int prod = SIZEOF_INT;
        List<Integer> revInput = new ArrayList<>(list);
        Collections.reverse(revInput);
        for (int num : revInput) {
            suffix.add(prod);
            prod *= num;
        }
        if (pointer) {
            suffix.add(prod);
        }
        Collections.reverse(suffix);
        return Collections.unmodifiableList(suffix);
    }

    public Symbol(String name, String field) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
        this.constant = false;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = Collections.emptyList();
    }

    public Symbol(String name, String field, boolean constant, int init) {
        this.name = name;
        this.field = field;
        this.type = Type.INT;
        this.constant = constant;
        this.initValue = init;
        this.dimSize = Collections.emptyList();
        this.initArray = Collections.emptyList();
        this.dimBase = Collections.emptyList();
    }

    public Symbol(String name, String field, List<Integer> dimSize) {
        this.name = name;
        this.field = field;
        this.type = Type.ARRAY;
        this.constant = false;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, false);
    }

    public Symbol(String name, String field, List<Integer> dimSize, boolean constant, List<Integer> init) {
        this.name = name;
        this.field = field;
        this.type = Type.ARRAY;
        this.constant = constant;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.unmodifiableList(init);
        this.dimBase = suffixProduct(dimSize, false);
    }

    public Symbol(String name, String field, List<Integer> dimSize, boolean constant) {
        this.name = name;
        this.field = field;
        this.type = Type.POINTER;
        this.constant = constant;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, true);
    }

    public Symbol(String name, String field, boolean constant) {
        this.name = name;
        this.field = field;
        this.type = Type.POINTER;
        this.constant = constant;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, true);
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

    public boolean hasAddress() {
        return address >= 0;
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

    public int getDimCount() {
        return dimSize.size();
    }

    public List<Integer> getDimSize() {
        return dimSize;
    }

    public int getSizeOfDim(int dim) {
        assert type.equals(Type.ARRAY) || type.equals(Type.POINTER);
        return dimSize.get(dim);
    }

    public int getBaseOfDim(int dim) {
        assert type.equals(Type.ARRAY) || type.equals(Type.POINTER);
        return dimBase.get(dim);
    }

    public int getBase() {
        assert type.equals(Type.ARRAY) || type.equals(Type.POINTER);
        return dimBase.isEmpty() ? SIZEOF_INT : dimBase.iterator().next();
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

    public Symbol toPointer() {
        assert type.equals(Type.ARRAY);
        ArrayList<Integer> reducedDimSize = new ArrayList<>();
        for (int i = 1; i < dimSize.size(); i++) {
            reducedDimSize.add(dimSize.get(i));
        }
        tempCount = tempCount + 1;
        Symbol sym = new Symbol("ptr_" + tempCount, field, reducedDimSize, constant);
        sym.setLocal(true);
        return sym;
    }

    public Symbol subPointer(int depth) {
        assert type.equals(Type.POINTER);
        ArrayList<Integer> reducedDimSize = new ArrayList<>();
        for (int i = depth; i < dimSize.size(); i++) {
            reducedDimSize.add(dimSize.get(i));
        }
        tempCount = tempCount + 1;
        Symbol sym = new Symbol("ptr_" + tempCount, field, reducedDimSize, constant);
        sym.setLocal(true);
        return sym;
    }

    @Override
    public String toString() {
        // String address = !hasAddress() ? "(tmp)" : ("@[" + (isLocal() ? "sp-" + getAddress() : "data+" + getAddress()) + "]");
        String address = !hasAddress() ? "(tmp)" : String.format(isLocal() ? "@[sp-0x%x]" : "@[data+0x%x]", getAddress());
        return name + address + ":" + type;
    }

    // 临时变量暂时不具备栈上空间
    private static int tempCount = 0;
    public static Symbol temporary(String field, Type type) {
        assert type.equals(Type.INT) || type.equals(Type.POINTER);
        tempCount = tempCount + 1;
        Symbol sym;
        if (type.equals(Type.POINTER)) {
            sym = new Symbol("ptr_" + tempCount, field, false);
        } else {
            sym = new Symbol("tmp_" + tempCount, field);
        }
        sym.setLocal(true);
        return sym;
    }
}
