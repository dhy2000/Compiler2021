package backend;

import backend.hardware.Memory;
import intermediate.Intermediate;
import intermediate.code.*;
import intermediate.symbol.FuncMeta;
import intermediate.symbol.Symbol;
import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.util.*;

/**
 * 中间代码翻译到 MIPS
 */
public class Translator {
    private final Intermediate ir;

    private final RegisterMap registerMap = new RegisterMap();
    private final Mips mips = new Mips();

    public Translator(Intermediate ir) {
        this.ir = ir;
    }

    public void loadStringConstant() {
        for (Map.Entry<String, String> entry : ir.getGlobalStrings().entrySet()) {
            mips.addStringConstant(entry.getKey(), entry.getValue());
        }
    }

    public void loadGlobals() {
        Map<String, Integer> globalAddress = ir.getGlobalAddress();
        Memory initMem = mips.getInitMem(); // Modifiable!
        // Global Variable
        for (Map.Entry<String, Integer> entry : ir.getGlobalVariables().entrySet()) {
            int address = globalAddress.get(entry.getKey());
            initMem.storeWord(address, entry.getValue());
        }
        // Global Array
        for (Map.Entry<String, List<Integer>> entry : ir.getGlobalArrays().entrySet()) {
            int baseAddress = globalAddress.get(entry.getKey());
            for (int i = 0; i < entry.getValue().size(); i++) {
                initMem.storeWord(baseAddress + Symbol.SIZEOF_INT * i, entry.getValue().get(i));
            }
        }
    }

    /* ------ 代码翻译 ------ */
    // 记录当前基本块中临时变量的 def-use 情况
    private final Map<Symbol, Integer> tempDefUse = new HashMap<>(); // key: 临时变量的 Symbol, value: 临时变量在当前基本块中还剩余几次被使用

    // 处理在当前基本块中临时变量的使用次数
    private void processTempVariableUses(BasicBlock block) {

    }

    private void consumeUseTempVariable(Symbol symbol) {
        assert tempDefUse.containsKey(symbol);
        int count = tempDefUse.get(symbol);
        if (count == 1) {
            tempDefUse.remove(symbol);
            // 如果不再使用的临时变量在寄存器中，应释放寄存器
            if (registerMap.isAllocated(symbol)) {
                registerMap.dealloc(registerMap.getRegisterOfSymbol(symbol));
            }
        } else {
            tempDefUse.put(symbol, count - 1);
        }
    }

    // 为临时变量分配栈上地址
    private void assignAddressForTemp(Symbol symbol) {
        currentStackSize += Symbol.SIZEOF_INT;
        symbol.setAddress(currentStackSize);
    }

    // 为变量分配寄存器
    private int allocRegister(Symbol symbol) {
        return 0;
    }


    private void translateBinaryOp(BinaryOp code) {
        // 需要为操作数符号分配寄存器, 还需要考虑操作数是变量还是立即数来选取指令
        // 如果是双立即数就直接算出结果，存给临时变量
        // 操作数变量和目标变量必须都已经分配上寄存器再进行计算
    }

    private void translateUnaryOp(UnaryOp code) {

    }

    private void translateIO(ILinkNode code) {
        assert code instanceof Input || code instanceof PrintInt || code instanceof PrintStr;

    }

    private void translateCall(Call code) {

    }

    private void translateReturn(Return code) {

    }

    private void translateAddressOffset(AddressOffset code) {

    }

    private void translatePointerOp(PointerOp code) {

    }

    private void translateBranchOrJump(ILinkNode code) {
        assert code instanceof Jump || code instanceof BranchIfElse;

    }

    // 记录当前正在翻译的函数
    private FuncMeta currentFunc = null;
    private boolean isMain = false;
    private int currentStackSize = 0; // 当前正在翻译的函数已经用掉的栈的大小（局部变量+临时变量）

    // BFS 基本块
    private final HashSet<BasicBlock> visitedBlock = new HashSet<>();
    private final Queue<BasicBlock> queueBlock = new LinkedList<>();

    public void translateBasicBlock(BasicBlock block) {

    }

    // 从函数头部开始, 将基本块中的中间代码翻译成 MIPS 目标代码
    public void translateFunction(FuncMeta meta) {
        currentFunc = meta;
        currentStackSize = meta.getStackSize();
        BasicBlock head = meta.getBody();
        queueBlock.offer(head);
        while (!queueBlock.isEmpty()) {
            BasicBlock block = queueBlock.poll();
            if (visitedBlock.contains(block)) {
                continue;
            }
            visitedBlock.add(block);
            translateBasicBlock(block);
        }
    }

    public Mips toMips() {
        loadStringConstant();
        loadGlobals();
        for (FuncMeta meta : ir.getFunctions().values()) {
            translateFunction(meta);
        }
        isMain = true;
        translateFunction(ir.getMainFunction());
        return mips;
    }
}
