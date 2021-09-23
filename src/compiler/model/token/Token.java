package compiler.model.token;

public abstract class Token {

    private final TokenType type;

    public Token(TokenType type) {
        this.type = type;
    }

    public String getTypeName() {
        return type.name();
    }

    public boolean isReserved() {
        return type.isReserved();
    }

    // non-reserve token must override this method
    public String getContent() {
        return type.getContent();
    }

    @Override
    public String toString() {
        return getContent();
    }
}
