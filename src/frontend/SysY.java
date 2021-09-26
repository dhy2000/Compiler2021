package frontend;

import config.Config;
import frontend.exceptions.UnrecognizedTokenException;
import frontend.source.Source;
import frontend.source.SourceReader;
import frontend.tokenize.TokenList;
import frontend.tokenize.Tokenizer;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;

    public SysY(InputStream src) {
        // read source Code
        Source source = new SourceReader(src).read();
        // to tokenize
        try {
            tokens = Tokenizer.tokenize(source);
            if (Config.hasOperationOutput(Config.Operation.TOKENIZE)) {
                tokens.forEach(token -> Config.getTarget().printf("%s %s\n", token.typeName(), token.getContent()));
            }
        } catch (UnrecognizedTokenException e) {
            Config.getTarget().println(e.getMessage());
            source.printAll(Config.getTarget());
        }

    }

    public TokenList getTokenList() {
        return tokens;
    }
}
