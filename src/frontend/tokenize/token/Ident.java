package frontend.tokenize.token;

public class Ident extends Token {

    private final String name;

    public Ident(String name, int line) {
        super(Type.IDENFR, line, name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
