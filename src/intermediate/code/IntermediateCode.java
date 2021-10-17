package intermediate.code;

/**
 * 抽象的中间代码基类
 */
public abstract class IntermediateCode {

    private final String function;
    private final String block;
    private final int index;

    public IntermediateCode(String function, String block, int index) {
        this.function = function;
        this.block = block;
        this.index = index;
    }

    public final String getFunction() {
        return function;
    }

    public final String getBlock() {
        return block;
    }

    public final int getIndex() {
        return index;
    }

    public final String getLabel() {
        return function + "_" + block + "_" + index;
    }

    public abstract String getName(); // 中间代码的种类

    @Override
    public abstract String toString();
}
