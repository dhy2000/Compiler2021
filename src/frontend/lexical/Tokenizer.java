package frontend.lexical;

import exception.UnrecognizedTokenException;
import frontend.input.Source;
import frontend.lexical.token.Token;

import java.util.Objects;

public class Tokenizer {
    private Tokenizer() {}

    public static TokenList tokenize(Source source)
            throws UnrecognizedTokenException {
        TokenList ts = new TokenList();

        while (!source.reachedEndOfFile()) {
            source.skipBlanks();
            // skip line comment
            if ("//".equals(source.followingSeq(2))) {
                source.nextLine();
                continue;
            }
            // skip block comment
            if ("/*".equals(source.followingSeq(2))) {
                source.forward(2);
                while (!source.reachedEndOfFile() && !"*/".equals(source.followingSeq(2))) {
                    source.forward(1);
                }
                if ("*/".equals(source.followingSeq(2))) {
                    source.forward(2);
                    continue;
                }
            }
            // get token
            boolean matchToken = false;
            for (Token.Type type : Token.Type.values()) {
                String token = source.matchFollowing(type.getPattern());
                if (Objects.nonNull(token)) {
                    ts.append(Token.newInstance(type, source.getLineIndex(), token));
                    source.forward(token.length());
                    matchToken = true;
                    break;
                }
            }
            if (!source.reachedEndOfFile() && !matchToken) {
                throw new UnrecognizedTokenException(source.getLineIndex(),
                        source.getCurrentLine());
            }
        }
        return ts;
    }
}
