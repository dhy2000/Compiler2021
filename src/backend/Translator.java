package backend;

import backend.hardware.Memory;
import intermediate.Intermediate;
import intermediate.symbol.FuncMeta;
import intermediate.symbol.Symbol;

import java.util.List;
import java.util.Map;

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

    public void translateFunction(FuncMeta meta, boolean main) {

    }

    public Mips toMips() {
        loadStringConstant();
        loadGlobals();
        for (FuncMeta meta : ir.getFunctions().values()) {
            translateFunction(meta, false);
        }

        translateFunction(ir.getMainFunction(), true);
        return mips;
    }
}
