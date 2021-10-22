package intermediate.optimize;

import intermediate.Intermediate;
import intermediate.code.*;
import intermediate.operand.Operand;
import intermediate.symbol.FuncMeta;

import java.util.*;

/**
 * 将 printf 转成 print_int 和 print_str
 */
public class PrintfTrans implements MidOptimizer {

    public PrintfTrans() {

    }

    @Override
    public void optimize(Intermediate ir) {
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>(); // BFS
        for (FuncMeta func : ir.getFunctions().values()) { // 遍历所有的函数
            queue.offer(func.getBody()); // 从函数头部的第一个 BasicBlock 开始搜索
            while (!queue.isEmpty()) {
                BasicBlock block = queue.poll();
                if (visited.contains(block)) {
                    continue;
                }
                visited.add(block); // 标记为访问过
                ILinkNode node = block.getHead();
                while (Objects.nonNull(node) && node.hasNext()) { // 顺序遍历当前 BasicBlock
                    if (node instanceof Jump) { // 对于分支和跳转语句，将其跳转目标记录到待搜索的队列中
                        queue.offer(((Jump) node).getTarget());
                    } else if (node instanceof BranchIfElse) {
                        queue.offer(((BranchIfElse) node).getThenTarget());
                        queue.offer(((BranchIfElse) node).getElseTarget());
                    } else { // 正式的处理
                        if (node instanceof PrintFormat) { // 将 printf 转化成输出整数和输出字符串
                            String format = ((PrintFormat) node).getFormat();
                            List<Operand> params = ((PrintFormat) node).getParams();
                            String[] parts = format.split("%d", -1); // use limit:-1 to keep empty strings
                            assert parts.length == params.size() + 1;
                            for (int i = 0; i < parts.length; i++) {
                                if (!parts[i].isEmpty()) {
                                    String label = ir.addGlobalString(parts[i]);
                                    node.insertBefore(new PrintStr(label));
                                }
                                if (i < params.size()) {
                                    node.insertBefore(new PrintInt(params.get(i)));
                                }
                            }
                            node.remove();
                            node = node.getNext();
                            continue;
                        }
                    }
                    node = node.getNext();
                }
            }
        }
    }
}
