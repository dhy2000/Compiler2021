package intermediate.code;


import intermediate.symbol.Symbol;

public class Input extends ILinkNode {
    private final Symbol dst;

    public Input(Symbol dst) {
        this.dst = dst;
    }

    public Symbol getDst() {
        return dst;
    }

    @Override
    public String toString() {
        return "INPUT " + dst;
    }
}
