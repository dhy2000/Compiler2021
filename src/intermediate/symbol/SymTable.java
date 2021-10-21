package intermediate.symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 符号表
 */
public class SymTable {

    private final String field; // 作用域
    private final Map<String, Symbol> symbols = new HashMap<>();
    private int capacity = 0;

    private final SymTable parent;

    private SymTable() {
        this.field = "GLOBAL";
        this.parent = null;
    }

    public static SymTable global() {
        return new SymTable();
    }

    public SymTable(String field, SymTable parent) {
        this.field = field;
        this.parent = parent;
    }

    public SymTable getParent() {
        return parent;
    }

    public boolean hasParent() {
        return Objects.nonNull(parent);
    }

    public String getField() {
        return field;
    }

    public int capacity() {
        return capacity;
    }

    public void add(Symbol symbol) {
        symbols.putIfAbsent(symbol.getName(), symbol);
        capacity += symbol.capacity();
    }

    public boolean contains(String name, boolean recursive) {
        if (symbols.containsKey(name)) {
            return true;
        } else {
            if (Objects.nonNull(parent) && recursive) {
                return parent.contains(name, true);
            }
            return false;
        }
    }

    public Symbol get(String name, boolean recursive) {
        Symbol symbol = symbols.get(name);
        if (Objects.nonNull(symbol)) {
            return symbol;
        } else if (recursive && Objects.nonNull(parent)) {
            return parent.get(name, true);
        }
        return null;
    }
}
