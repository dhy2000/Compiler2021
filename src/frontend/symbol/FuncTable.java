package frontend.symbol;

import frontend.symbol.type.BasicType;
import frontend.symbol.type.VarType;

import java.util.*;

/**
 * 函数的符号表
 */
public class FuncTable {
    public static class Item {
        private final String name;
        private final BasicType returnType; // Nullable, `void` if null
        private final List<VarType> paramTypes;

        public Item(String name) {
            this.name = name;
            this.returnType = null;
            this.paramTypes = Collections.emptyList();
        }

        public Item(String name, BasicType returnType) {
            this.name = name;
            this.returnType = returnType;
            this.paramTypes = Collections.emptyList();
        }

        public Item(String name, List<VarType> paramTypes) {
            this.name = name;
            this.returnType = null;
            this.paramTypes = Collections.unmodifiableList(paramTypes);
        }

        public Item(String name, BasicType returnType, List<VarType> paramTypes) {
            this.name = name;
            this.returnType = returnType;
            this.paramTypes = Collections.unmodifiableList(paramTypes);
        }

        public String getName() {
            return name;
        }

        public BasicType getReturnType() {
            return returnType;
        }

        public List<VarType> getParamTypes() {
            return paramTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return Objects.equals(name, item.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    private static class InstanceHolder {
        private static final FuncTable INSTANCE = new FuncTable();
    }

    private FuncTable() {}

    public static FuncTable getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final Map<String, Item> items = new HashMap<>();

    public void add(Item item) {
        items.put(item.getName(), item);
    }

    public Item getByName(String name) {
        return items.get(name);
    }

    public boolean contains(String name) {
        return items.containsKey(name);
    }
}
