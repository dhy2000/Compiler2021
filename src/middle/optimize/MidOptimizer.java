package middle.optimize;

import middle.MiddleCode;

/**
 * 中间代码优化的基本接口
 */
public interface MidOptimizer {
    void optimize(MiddleCode ir);
}
