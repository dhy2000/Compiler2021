package intermediate.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncDef {
    private final String name;
    private final SymTable paramTable;
    private final List<Symbol> params = new ArrayList<>();


    public FuncDef(String name, SymTable global) {
        this.name = name;
        this.paramTable = new SymTable(name, global);
    }

    public String getName() {
        return name;
    }

    public SymTable getParamTable() {
        return paramTable;
    }

    public List<Symbol> getParams() {
        return Collections.unmodifiableList(params);
    }

    public void addParam(Symbol param) {
        paramTable.add(param);
        params.add(param);
    }
}
