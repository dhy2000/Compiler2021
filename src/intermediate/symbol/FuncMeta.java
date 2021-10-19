package intermediate.symbol;

import intermediate.code.BasicBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncMeta {
    private final String name;
    private final SymTable paramTable;
    private final List<Symbol> params = new ArrayList<>();
    private BasicBlock body;

    public enum ReturnType {
        INT,
        VOID
    }

    private final ReturnType type;


    public FuncMeta(String name, ReturnType type, SymTable global) {
        this.name = name;
        this.paramTable = new SymTable(name, global);
        this.type = type;
    }

    public ReturnType getReturnType() {
        return type;
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

    public void loadBody(BasicBlock body) {
        this.body = body;
    }

    public BasicBlock getBody() {
        return body;
    }
}
