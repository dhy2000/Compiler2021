package frontend.symbol.type;

import java.util.Collections;
import java.util.List;

public class ArrayType implements VarType {
    private final BasicType type;

    private final List<Integer> innerDims; // 除最外维以外每一维的长度

    public ArrayType(BasicType type, List<Integer> innerDims) {
        this.type = type;
        this.innerDims = Collections.unmodifiableList(innerDims);
    }

    public ArrayType(BasicType type) {
        this.type = type;
        this.innerDims = Collections.emptyList();
    }

    public int getDimCount() {
        return innerDims.size() + 1;
    }

    public int getSizeOfInner(int dim) {
        return innerDims.get(dim);
    }
}
