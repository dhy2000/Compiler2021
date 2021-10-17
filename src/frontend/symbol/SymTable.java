package frontend.symbol;

import frontend.symbol.type.ArrayType;
import frontend.symbol.type.BasicType;
import frontend.symbol.type.VarType;

import java.util.*;

/**
 * 变量的符号表
 */
public class SymTable {

    private static final SymTable GLOBAL = new SymTable();

    public static class Item {
        private final String name;
        private final VarType type;
        private final boolean constant;
        private boolean modified = false;

        private final Integer initValue;
        private final List<Integer> initArray;

        public Item(String name, VarType type) {
            this.name = name;
            this.type = type;
            this.constant = false;
            this.initValue = null;
            this.initArray = Collections.emptyList();
        }

        public Item(String name, VarType type, boolean constant, int value) {
            this.name = name;
            this.type = type;
            this.constant = constant;
            this.initValue = value;
            this.initArray = Collections.emptyList();
        }

        public Item(String name, VarType type, boolean constant, List<Integer> values) {
            this.name = name;
            this.type = type;
            this.constant = constant;
            this.initValue = null;
            this.initArray = Collections.unmodifiableList(values);
        }

        public String getName() {
            return name;
        }

        public VarType getType() {
            return type;
        }

        public boolean isConstant() {
            return constant;
        }

        public boolean hasInitValue() {
            return Objects.nonNull(initValue);
        }

        public boolean hasInitArray() {
            return !initArray.isEmpty();
        }

        public Integer getInitValue() {
            return initValue;
        }

        public List<Integer> getInitArray() {
            return initArray;
        }

        public boolean isModified() {
            return modified;
        }

        public void modify() {
            modified = true;
        }

        public boolean isInitialized() {
            if (type instanceof ArrayType) {
                return !initArray.isEmpty();
            } else if (type instanceof BasicType) {
                return Objects.nonNull(initValue);
            } else {
                throw new AssertionError("Symbol Type Error");
            }
        }
    }

    private final String field;
    private final SymTable parent;

    public SymTable() {
        this.field = "@GLOBAL";
        this.parent = null;
    }

    public SymTable(SymTable parent, String field) {
        this.parent = parent;
        this.field = field;
    }

    private final Map<String, Item> symbols = new HashMap<>();

    public String getField() {
        return field;
    }

    public boolean hasParent() {
        return Objects.nonNull(parent);
    }

    public SymTable getParent() {
        return parent;
    }

    public void add(Item item) {
        symbols.put(item.getName(), item);
    }

    public boolean contains(String name) {
        return symbols.containsKey(name) || (Objects.nonNull(parent) && parent.contains(name));
    }

    public Item getItemByName(String name) {
        Item item = symbols.get(name);
        if (Objects.isNull(item)) {
            return Objects.nonNull(parent) ? parent.getItemByName(name) : null;
        }
        return item;
    }

    public String getFieldByName(String name) {
        if (contains(name)) {
            return field;
        } else if (Objects.nonNull(parent)) {
            return parent.getFieldByName(name);
        } else {
            return null;
        }
    }

    public static SymTable getGlobal() {
        return GLOBAL;
    }
}
