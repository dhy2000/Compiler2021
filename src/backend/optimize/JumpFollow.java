package backend.optimize;

import backend.Mips;
import backend.instruction.JumpLabel;
import backend.instruction.MipsInstruction;

import java.util.Objects;

public class JumpFollow implements MipsOptimize {

    public JumpFollow() {}

    @Override
    public void optimize(Mips mips) {
        MipsInstruction instr = mips.getFirstInstruction();
        while (Objects.nonNull(instr) && instr.hasNext()) {
            if (instr instanceof JumpLabel && !instr.hasLabel()
                    && instr.getJumpTarget().equals(((MipsInstruction) instr.getNext()).getLabel())) {
                instr.remove();
            }
            instr = (MipsInstruction) instr.getNext();
        }
    }
}
