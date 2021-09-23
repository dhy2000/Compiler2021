package compiler.model.token;

public class Ident extends Token {

    /**
     * 必须满足标识符的要求
     */
    private final String identifier;

    public Ident(String identifier) {
        super(TokenType.IDENFR);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getContent() {
        return identifier;
    }
}
