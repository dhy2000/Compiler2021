package frontend;

import config.Config;
import frontend.error.exception.FrontendException;
import frontend.source.Source;
import frontend.source.SourceReader;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;

    public SysY(InputStream src) {
        // read source Code
        Source source = new SourceReader(src).read();
        try {
            // tokenize
            tokens = Tokenizer.tokenize(source);
            if (Config.hasOperationOutput(Config.Operation.TOKENIZE)) {
                tokens.output(Config.getTarget());
            }
            // syntax parse
            CompUnit compUnit = new CompUnitParser(tokens).parseCompUnit();
            if (Config.hasOperationOutput(Config.Operation.SYNTAX_PARSE)) {
                compUnit.output(Config.getTarget());
            }
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
