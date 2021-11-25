package middle.optimize;

import middle.MiddleCode;
import middle.code.BasicBlock;
import middle.code.BranchIfElse;
import middle.code.ILinkNode;
import middle.code.Jump;
import middle.symbol.FuncMeta;

import java.util.*;

/**
 * 优化掉没用的跳转，如果一个跳转跳到下一个基本块，且没有其他来源可以跳到当前跳转的目标基本块，则将跳转语句去掉，直接和目标基本块合并
 * 例如：j label1 ; label1: something
 */
public class MergeBlock implements MidOptimizer {

    public MergeBlock() {

    }

    // Block -> 来源数目
    private Map<BasicBlock, Integer> blockSources = new HashMap<>();

    /**
     * 先 BFS 遍历，对每个基本块记录其有几个来源
     * @param ir 需要优化的中间代码
     */
    @Override
    public void optimize(MiddleCode ir) {
        // 先 BFS 搜一遍每个基本块的来源数目
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>();
        for (FuncMeta func : ir.getFunctions().values()) {
            queue.offer(func.getBody());
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                if (visited.contains(block)) {
                    continue;
                }
                visited.add(block);
                ILinkNode node = block.getHead();
                while (Objects.nonNull(node) && node.hasNext()) {
                    if (node instanceof Jump) {
                        queue.offer(((Jump) node).getTarget());
                        blockSources.merge(((Jump) node).getTarget(), 1, Integer::sum);
                    } else if (node instanceof BranchIfElse) {
                        queue.offer(((BranchIfElse) node).getThenTarget());
                        queue.offer(((BranchIfElse) node).getElseTarget());
                        blockSources.merge(((BranchIfElse) node).getThenTarget(), 1, Integer::sum);
                        blockSources.merge(((BranchIfElse) node).getElseTarget(), 1, Integer::sum);
                    }
                    node = node.getNext();
                }
            }
        }
        // 再次遍历所有的基本块
        visited.clear();
        for (FuncMeta func : ir.getFunctions().values()) {
            queue.offer(func.getBody());
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                if (visited.contains(block)) {
                    continue;
                }
                visited.add(block);
                ILinkNode node = block.getHead();
                while (Objects.nonNull(node) && node.hasNext()) {
                    if (node instanceof Jump) {
                        BasicBlock target = ((Jump) node).getTarget();
                        if (blockSources.containsKey(target) && blockSources.get(target).equals(1)) {
                            // 合并基本块
                            ILinkNode tail = target.getTail();
                            tail.setNext(null);
                            block.append(target.getHead());
                            node.remove();
                        } else {
                            queue.offer(target);
                        }
                    } else if (node instanceof BranchIfElse) {
                        queue.offer(((BranchIfElse) node).getThenTarget());
                        queue.offer(((BranchIfElse) node).getElseTarget());
                    }
                    node = node.getNext();
                }
            }
        }
    }
}
