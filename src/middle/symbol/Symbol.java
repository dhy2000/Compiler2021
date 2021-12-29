package middle.symbol;

import middle.operand.Operand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 符号表中的符号(表项)
 */
public class Symbol implements Operand {
    public static final int SIZEOF_INT = 4;

    private final String name;

    public enum BasicType {
        INT(4),
        SHORT(2),
        CHAR(1),
        ;

        private final int size;

        BasicType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public enum RefType {
        ITEM,
        ARRAY,      // 数组
        POINTER,    // 作为函数参数或者数组寻址后的临时变量，**比数组少一维**！
    }

    private final BasicType basicType;
    private final RefType refType;
    private boolean local; // 是否为局部变量，如果是则基地址为当前运行栈栈底；如果否则基地址为全局空间头部
    private int address = -1; // 相对基地址的位移

    private final boolean constant;
    private final List<Integer> dimSize;    // 每一维的长度，如果是指针则少一维
    private final List<Integer> dimBase;    // dimSize 的后缀积

    private final Integer initValue;        // 整数变量的初值
    private final List<Integer> initArray;  // 数组的初值（展平了的）

    private static List<Integer> suffixProduct(List<Integer> list, int basicSize, boolean pointer) {
        List<Integer> suffix = new ArrayList<>();
        int prod = basicSize;
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

    public Symbol(String name, BasicType type) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.ITEM;
        this.constant = false;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = Collections.emptyList();
    }

    public Symbol(String name, BasicType type, boolean constant, int init) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.ITEM;
        this.constant = constant;
        this.initValue = init;
        this.dimSize = Collections.emptyList();
        this.initArray = Collections.emptyList();
        this.dimBase = Collections.emptyList();
    }

    public Symbol(String name, BasicType type, List<Integer> dimSize) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.ARRAY;
        this.constant = false;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, type.size, false);
    }

    public Symbol(String name, BasicType type, List<Integer> dimSize, boolean constant, List<Integer> init) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.ARRAY;
        this.constant = constant;
        this.initValue = null;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initArray = Collections.unmodifiableList(init);
        this.dimBase = suffixProduct(dimSize, type.size,false);
    }

    public Symbol(String name, BasicType type, List<Integer> dimSize, boolean constant) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.POINTER;
        this.constant = constant;
        this.dimSize = Collections.unmodifiableList(dimSize);
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, type.size, true);
    }

    public Symbol(String name, BasicType type, boolean constant) {
        this.name = name;
        this.basicType = type;
        this.refType = RefType.POINTER;
        this.constant = constant;
        this.dimSize = Collections.emptyList();
        this.initValue = null;
        this.initArray = Collections.emptyList();
        this.dimBase = suffixProduct(dimSize, type.size,true);
    }


    public String getName() {
        return name;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public RefType getRefType() {
        return refType;
    }

    public BasicType getBasicType() {
        return basicType;
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
        assert refType.equals(RefType.ARRAY) || refType.equals(RefType.POINTER);
        return dimSize.get(dim);
    }

    public int getBaseOfDim(int dim) {
        assert refType.equals(RefType.ARRAY) || refType.equals(RefType.POINTER);
        return dimBase.get(dim);
    }

    public int getBase() {
        assert refType.equals(RefType.ARRAY) || refType.equals(RefType.POINTER);
        return dimBase.isEmpty() ? SIZEOF_INT : dimBase.iterator().next();
    }

    public Integer getInitValue() {
        return initValue;
    }

    public List<Integer> getInitArray() {
        return initArray;
    }

    public int capacity() {
        if (refType.equals(RefType.ITEM)) {
            return SIZEOF_INT;
        } else if (refType.equals(RefType.POINTER)) {
            return SIZEOF_INT;
        } else {
            return SIZEOF_INT * dimSize.stream().reduce((i, i2) -> i * i2).orElse(1);
        }
    }

    public Symbol toPointer() {
        assert refType.equals(RefType.ARRAY);
        ArrayList<Integer> reducedDimSize = new ArrayList<>();
        for (int i = 1; i < dimSize.size(); i++) {
            reducedDimSize.add(dimSize.get(i));
        }
        tempCount = tempCount + 1;
        Symbol sym = new Symbol("ptr_" + tempCount, basicType, reducedDimSize, constant);
        sym.setLocal(true);
        return sym;
    }

    public Symbol subPointer(int depth) {
        assert refType.equals(RefType.POINTER);
        ArrayList<Integer> reducedDimSize = new ArrayList<>();
        for (int i = depth; i < dimSize.size(); i++) {
            reducedDimSize.add(dimSize.get(i));
        }
        tempCount = tempCount + 1;
        Symbol sym = new Symbol("ptr_" + tempCount, basicType, reducedDimSize, constant);
        sym.setLocal(true);
        return sym;
    }

    @Override
    public String toString() {
        // String address = !hasAddress() ? "(tmp)" : ("@[" + (isLocal() ? "sp-" + getAddress() : "data+" + getAddress()) + "]");
        String address = !hasAddress() ? "(tmp)" : String.format(isLocal() ? "@[sp-0x%x]" : "@[data+0x%x]", getAddress());
        return name + address + ":" + refType;
    }

    // 临时变量暂时不具备栈上空间
    private static int tempCount = 0;
    public static Symbol temporary(BasicType type, RefType refType) {
        assert refType.equals(RefType.ITEM) || refType.equals(RefType.POINTER);
        tempCount = tempCount + 1;
        Symbol sym;
        if (refType.equals(RefType.POINTER)) {
            sym = new Symbol("ptr_" + tempCount, type, false);
        } else {
            sym = new Symbol("tmp_" + tempCount, type);
        }
        sym.setLocal(true);
        return sym;
    }
}
