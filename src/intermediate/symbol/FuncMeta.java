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
    private int stackSize = 0; // 所有局部变量所占的空间(如临时变量需要分配空间必须在所有局部变量之后)
    private final boolean main;

    public enum ReturnType {
        INT,
        VOID
    }

    private final ReturnType type;


    public FuncMeta(String name, ReturnType type, SymTable global) {
        this.name = name;
        this.paramTable = new SymTable(name, global);
        this.type = type;
        this.main = false;
    }

    public FuncMeta(SymTable global) {
        this.name = "main";
        this.paramTable = new SymTable(this.name, global);
        this.type = ReturnType.INT;
        this.main = true;
    }

    public boolean isMain() {
        return main;
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
        updateStackSize(paramTable.capacity());
    }

    public void loadBody(BasicBlock body) {
        this.body = body;
    }

    public BasicBlock getBody() {
        return body;
    }

    public void updateStackSize(int size) {
        stackSize = Math.max(stackSize, size);
    }

    public int getStackSize() {
        return stackSize;
    }
}
