package intermediate.optimize;

import intermediate.Intermediate;

/**
 * 优化掉没用的跳转，如果一个跳转跳到下一个基本块，且没有其他来源可以跳到当前跳转的目标基本块，则将跳转语句去掉，直接和目标基本块合并
 * 例如：j label1 ; label1: something
 */
public class MergeBlock implements MidOptimizer {

    /**
     * 先 BFS 遍历，对每个基本块记录其有几个来源
     * @param ir 需要优化的中间代码
     */
    @Override
    public void optimize(Intermediate ir) {

    }
}
