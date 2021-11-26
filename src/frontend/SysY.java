package frontend;

import compiler.Config;
import exception.FrontendException;
import frontend.error.ErrorTable;
import frontend.input.Source;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;
import frontend.visitor.Visitor;
import middle.MiddleCode;

import java.io.InputStream;

public class SysY {

    private TokenList tokens;
    private CompUnit compUnit;
    private MiddleCode middleCode;

    public SysY(Config config) {
        InputStream src = config.getSource();
        // read source Code
        Source source = new Source(src);
        try {
            // tokenize
            tokens = Tokenizer.tokenize(source);
            if (config.hasTarget(Config.Operation.TOKENIZE)) {
                tokens.output(config.getTarget(Config.Operation.TOKENIZE));
            }
            // syntax parse
            compUnit = new CompUnitParser(tokens).parseCompUnit();
            if (config.hasTarget(Config.Operation.SYNTAX)) {
                compUnit.output(config.getTarget(Config.Operation.SYNTAX));
            }
            // generate intermediate code
            Visitor visitor = new Visitor();
            visitor.analyseCompUnit(compUnit);
            ErrorTable errors = visitor.getErrorTable();
            if (config.hasTarget(Config.Operation.ERROR)) {
                errors.forEach(error -> config.getTarget(Config.Operation.ERROR).println(error.getLineNum() + " " + error.getErrorTag()));
            }
            if (!errors.isEmpty()) {
                return;
            }
            middleCode = visitor.getIntermediate();
        } catch (FrontendException e) {
            if (config.hasTarget(Config.Operation.EXCEPTION)) {
                config.getTarget(Config.Operation.EXCEPTION).println(e.getMessage());
                source.printAll(config.getTarget(Config.Operation.EXCEPTION));
            } else {
                System.err.println(e.getMessage());
                source.printAll(System.err);
            }
        } catch (Exception e) {
            if (config.hasTarget(Config.Operation.EXCEPTION)) {
                config.getTarget(Config.Operation.EXCEPTION).println(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace(config.getTarget(Config.Operation.EXCEPTION));
            } else {
                System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public TokenList getTokenList() {
        return tokens;
    }

    public CompUnit getCompUnit() {
        return compUnit;
    }

    public MiddleCode getIntermediate() {
        return middleCode;
    }
}
