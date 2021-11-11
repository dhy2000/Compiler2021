package backend;

import backend.hardware.Memory;
import intermediate.Intermediate;
import intermediate.code.*;
import intermediate.symbol.FuncMeta;
import intermediate.symbol.Symbol;

import java.util.*;

/**
 * 中间代码翻译到 MIPS
 */
public class Translator {
    private final Intermediate ir;

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
    private void translateBinaryOp(BinaryOp code) {

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

    // BFS 基本块
    private final HashSet<BasicBlock> visitedBlock = new HashSet<>();
    private final Queue<BasicBlock> queueBlock = new LinkedList<>();

    public void translateBasicBlock(BasicBlock block) {

    }

    // 从函数头部开始, 将基本块中的中间代码翻译成 MIPS 目标代码
    public void translateFunction(FuncMeta meta) {
        currentFunc = meta;
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
