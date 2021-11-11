package backend;

import intermediate.symbol.Symbol;

import java.util.*;

/**
 * 描述当前的寄存器分配状况的表, 将寄存器当成 Cache
 * 功能：查询是否有可用的寄存器, 将寄存器分配给符号, 查看符号是否有对应寄存器, 置换出寄存器用于分配
 */
public class RegisterMap {

    // 可用来自由分配的寄存器：从 a0($4) 到 t9($25); v0($2) 作为函数返回值, v1($3) 可用来存储一个立即数
    private static final Collection<Integer> allocatableRegisters = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
    )));

    // 当前未使用(可自由分配)的寄存器
    private final Set<Integer> freeRegisters = new HashSet<>(allocatableRegisters);

    // 已经分配出去的寄存器对应的符号
    private final Map<Integer, Symbol> allocatedRegisters = new HashMap<>();
    // 符号对应到寄存器
    private final Map<Symbol, Integer> symbolToRegister = new HashMap<>();

    // 模拟 LRU 实现 Cache 置换
    private final LinkedHashSet<Integer> registerCache = new LinkedHashSet<>();

    public RegisterMap() {

    }

    // 当前是否有未使用的寄存器
    public boolean hasFreeRegister() {
        return !freeRegisters.isEmpty();
    }

    // 为变量符号分配寄存器（必须有自由寄存器）, 返回分配的寄存器号
    public int allocRegister(Symbol symbol) {
        // 一个变量只能占用一个寄存器
        if (symbolToRegister.containsKey(symbol)) {
            return symbolToRegister.get(symbol);
        }
        // 如果没有富余寄存器则报错
        if (freeRegisters.isEmpty()) {
            throw new AssertionError("No free registers!");
        }
        // 获取一个空闲寄存器
        int register = freeRegisters.iterator().next();
        freeRegisters.remove(register);
        // 将寄存器分配给符号
        assert !allocatedRegisters.containsKey(register);
        allocatedRegisters.put(register, symbol);
        // 记录符号对应的寄存器
        symbolToRegister.put(symbol, register);
        // 将寄存器置入 Cache
        assert !registerCache.contains(register);
        registerCache.add(register);
        return register;
    }

    // 符号是否分配过寄存器
    public boolean isAllocated(Symbol symbol) {
        return symbolToRegister.containsKey(symbol);
    }

    // 获取符号当前所在的寄存器
    public int getRegisterOfSymbol(Symbol symbol) {
        if (!symbolToRegister.containsKey(symbol)) {
            throw new AssertionError(String.format("%s not assigned register!", symbol.toString()));
        }
        return symbolToRegister.get(symbol);
    }

    // 获取可被换出的寄存器编号
    public int registerToReplace() {
        assert !registerCache.isEmpty();
        return registerCache.iterator().next();
    }

    // 取消某个寄存器的分配
    public Symbol dealloc(int register) {
        if (!allocatedRegisters.containsKey(register)) {
            return null;
        }
        freeRegisters.add(register);
        Symbol symbol = allocatedRegisters.remove(register);
        symbolToRegister.remove(symbol);
        registerCache.remove(register);
        return symbol;
    }

    // 获取当前总共的分配状态
    public Map<Integer, Symbol> getState() {
        return Collections.unmodifiableMap(allocatedRegisters);
    }

    // 清空分配状态
    public void clear() {
        allocatedRegisters.clear();
        symbolToRegister.clear();
        registerCache.clear();
        freeRegisters.clear();
        freeRegisters.addAll(allocatableRegisters);
    }

    // 更新 LRU
    public void refresh(Symbol symbol) {
        int register = symbolToRegister.get(symbol); // Nullable
        assert registerCache.contains(register);
        registerCache.remove(register);
        registerCache.add(register);
    }
}
