package frontend.symbol;

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

        public Item(String name, VarType type, boolean constant) {
            this.name = name;
            this.type = type;
            this.constant = constant;
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
    }

    private final SymTable parent;

    public SymTable() {
        this.parent = null;
    }

    public SymTable(SymTable parent) {
        this.parent = parent;
    }

    private final Map<String, Item> symbols = new HashMap<>();

    public void add(Item item) {
        symbols.put(item.getName(), item);
    }

    public boolean contains(String name) {
        return symbols.containsKey(name) || (Objects.nonNull(parent) && parent.contains(name));
    }

    public Item getByName(String name) {
        Item item = symbols.get(name);
        if (Objects.isNull(item)) {
            return Objects.nonNull(parent) ? parent.getByName(name) : null;
        }
        return item;
    }

    public static SymTable getGlobal() {
        return GLOBAL;
    }
}
