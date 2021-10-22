package frontend;

import config.Config;
import exception.FrontendException;
import frontend.generate.CodeGenerator;
import frontend.error.ErrorTable;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;
import input.Source;
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
            CodeGenerator codeGenerator = new CodeGenerator();
            codeGenerator.analyseCompUnit(compUnit);
            if (Config.hasOperationOutput(Config.Operation.ERROR)) {
                ErrorTable.getInstance().forEach(error -> Config.getTarget().println(error.getLineNum() + " " + error.getErrorTag()));
            }
            if (!ErrorTable.getInstance().isEmpty()) {
                return;
            }
            Intermediate ir = codeGenerator.getIntermediate();
            if (Config.hasOperationOutput(Config.Operation.INTERMEDIATE)) {
                ir.output(Config.getTarget());
            }
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
}
