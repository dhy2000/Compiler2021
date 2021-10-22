package intermediate.optimize;

import intermediate.Intermediate;

/**
 * 中间代码优化的基本接口
 */
public interface MidOptimizer {
    void optimize(Intermediate ir);
}
