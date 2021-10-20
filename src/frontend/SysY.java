package frontend;

import config.Config;
import exception.FrontendException;
import frontend.analyse.Analyzer;
import frontend.error.ErrorTable;
import input.Source;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;
import intermediate.Intermediate;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;
    private CompUnit compUnit;

    public SysY(InputStream src) {
        // read source Code
        Source source = new Source(src);
        try {
            // tokenize
            tokens = Tokenizer.tokenize(source);
            if (Config.hasOperationOutput(Config.Operation.TOKENIZE)) {
                tokens.output(Config.getTarget());
            }
            // syntax parse
            compUnit = new CompUnitParser(tokens).parseCompUnit();
            if (Config.hasOperationOutput(Config.Operation.SYNTAX)) {
                compUnit.output(Config.getTarget());
            }
            // generate intermediate code
            Analyzer analyzer = new Analyzer();
            analyzer.analyseCompUnit(compUnit);
            if (Config.hasOperationOutput(Config.Operation.ERROR)) {
                ErrorTable.getInstance().forEach(error -> Config.getTarget().println(error.getLineNum() + " " + error.getErrorTag()));
            }
        } catch (FrontendException e) {
            Config.getTarget().println(e.getMessage());
            source.printAll(Config.getTarget());
        } catch (Exception e) {
            Config.getTarget().println(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    public TokenList getTokenList() {
        return tokens;
    }

    public CompUnit getCompUnit() {
        return compUnit;
    }
}
