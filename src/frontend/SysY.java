package frontend;

import config.Config;
import frontend.error.exception.FrontendException;
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
        try {
            // tokenize
            tokens = Tokenizer.tokenize(source);
            tokens.output(Config.getTarget());
            // syntax parse

        } catch (FrontendException e) {
            Config.getTarget().println(e.getMessage());
            source.printAll(Config.getTarget());
        } catch (Exception e) {
            Config.getTarget().println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public TokenList getTokenList() {
        return tokens;
    }
}
