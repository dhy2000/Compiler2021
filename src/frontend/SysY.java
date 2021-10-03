package frontend;

import config.Config;
import frontend.exceptions.tokenize.UnrecognizedTokenException;
import frontend.source.Source;
import frontend.source.SourceReader;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;

    public SysY(InputStream src) {
        // read source Code
        Source source = new SourceReader(src).read();
        // to tokenize
        try {
            tokens = Tokenizer.tokenize(source);
            tokens.output(Config.getTarget());
        } catch (UnrecognizedTokenException e) {
            Config.getTarget().println(e.getMessage());
            source.printAll(Config.getTarget());
        }

    }

    public TokenList getTokenList() {
        return tokens;
    }
}
