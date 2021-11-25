package frontend;

import config.Config;
import exception.FrontendException;
import frontend.visitor.Visitor;
import frontend.error.ErrorTable;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;
import frontend.input.Source;
import intermediate.Intermediate;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;
    private CompUnit compUnit;
    private Intermediate intermediate;

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
            Visitor visitor = new Visitor();
            visitor.analyseCompUnit(compUnit);
            ErrorTable errors = visitor.getErrorTable();
            if (Config.hasOperationOutput(Config.Operation.ERROR)) {
                errors.forEach(error -> Config.getTarget().println(error.getLineNum() + " " + error.getErrorTag()));
            }
            if (!errors.isEmpty()) {
                return;
            }
            intermediate = visitor.getIntermediate();
        } catch (FrontendException e) {
            Config.getTarget().println(e.getMessage());
            source.printAll(Config.getTarget());
        } catch (Exception e) {
            Config.getTarget().println(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public TokenList getTokenList() {
        return tokens;
    }

    public CompUnit getCompUnit() {
        return compUnit;
    }

    public Intermediate getIntermediate() {
        return intermediate;
    }
}
