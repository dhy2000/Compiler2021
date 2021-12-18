package middle.optimize;

import middle.MiddleCode;
import middle.code.*;
import middle.symbol.FuncMeta;
import middle.symbol.Symbol;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * 合并一个 block 内多余的 move 指令
 *
 * 例如: ADD a, b, tmp3; MOV tmp3, c ==> ADD a, b, c
 */
public class ReduceMov implements MidOptimizer {

    public ReduceMov() {

    }

    @Override
    public void optimize(MiddleCode ir) {
        HashSet<BasicBlock> visited = new HashSet<>();
        Queue<BasicBlock> queue = new LinkedList<>(); // BFS
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
                    detectBranch(node, queue);
                    // Inside a block
                    if (!(node instanceof UnaryOp && ((UnaryOp) node).getOp().equals(UnaryOp.Op.MOV))) {
                        node = node.getNext();
                        continue;
                    }
                    // Only deal with MOV
                    // mov tmp3 -> tmp4, use tmp4 to replace def(tmp3)
                    UnaryOp mov = (UnaryOp) node;
                    // source of mov must be temporary variable
                    if (!(mov.getSrc() instanceof Symbol && !(((Symbol) mov.getSrc()).hasAddress()))) {
                        node = node.getNext();
                        continue;
                    }
                    assert mov.getSrc() instanceof Symbol && !(((Symbol) mov.getSrc()).hasAddress());
                    Symbol src = (Symbol) mov.getSrc();
                    Symbol dst = mov.getDst();
                    mov.remove();
                    ILinkNode prev = mov.getPrev();
                    while (Objects.nonNull(prev) && prev.hasPrev()) {
                        // BinaryOp, UnaryOp, Input, AddressOffset
                        if (prev instanceof BinaryOp) {
                            if (((BinaryOp) prev).getDst().equals(src)) {
                                BinaryOp another = new BinaryOp(((BinaryOp) prev).getOp(), ((BinaryOp) prev).getSrc1(), ((BinaryOp) prev).getSrc2(), dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        } else if (prev instanceof UnaryOp) {
                            if (((UnaryOp) prev).getDst().equals(src)) {
                                UnaryOp another = new UnaryOp(((UnaryOp) prev).getOp(), ((UnaryOp) prev).getSrc(), dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        } else if (prev instanceof Input) {
                            if (((Input) prev).getDst().equals(src)) {
                                Input another = new Input(dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        } else if (prev instanceof AddressOffset) {
                            if (((AddressOffset) prev).getTarget().equals(src)) {
                                AddressOffset another = new AddressOffset(((AddressOffset) prev).getBase(), ((AddressOffset) prev).getOffset(), dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        } else if (prev instanceof PointerOp && ((PointerOp) prev).getOp().equals(PointerOp.Op.LOAD)) {
                            if (((PointerOp) prev).getDst().equals(src)) {
                                PointerOp another = new PointerOp(((PointerOp) prev).getOp(), ((PointerOp) prev).getAddress(), dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        } else if (prev instanceof Call && ((Call) prev).hasRet()) {
                            if (((Call) prev).getRet().equals(src)) {
                                Call another = new Call(((Call) prev).getFunction(), ((Call) prev).getParams(), dst);
                                prev.insertBefore(another);
                                prev.remove();
                                prev = prev.getPrev();
                                continue;
                            }
                        }
                        prev = prev.getPrev();
                    }
                    node = node.getNext();
                }
            }
        }
    }
}
