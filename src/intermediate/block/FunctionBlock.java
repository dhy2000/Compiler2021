package intermediate.block;

import intermediate.code.IntermediateCode;

import java.util.Objects;

/**
 * 函数定义块
 */
public class FunctionBlock {
    private final String name;
    private IntermediateCode entry;

    public FunctionBlock(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IntermediateCode getEntry() {
        return entry;
    }

    public void setEntry(IntermediateCode entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionBlock that = (FunctionBlock) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
