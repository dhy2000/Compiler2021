package frontend;

import backend.Mips;
import compiler.Config;
import compiler.SpecialOptimize;
import exception.ConstExpException;
import exception.FrontendException;
import exception.UndefinedTokenException;
import exception.WrongTokenException;
import frontend.error.ErrorTable;
import frontend.input.Source;
import frontend.lexical.TokenList;
import frontend.lexical.Tokenizer;
import frontend.syntax.CompUnit;
import frontend.syntax.CompUnitParser;
import frontend.visitor.Visitor;
import middle.MiddleCode;
import utility.MathUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

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
                throw new AssertionError("error found!");
            }
            middleCode = visitor.getIntermediate();
        } catch (FrontendException e) {
            if (config.hasTarget(Config.Operation.EXCEPTION)) {
                config.getTarget(Config.Operation.EXCEPTION).println(e.getMessage());
                source.printAll(config.getTarget(Config.Operation.EXCEPTION));
            } else {
                System.err.println(e.getMessage());
                source.printAll(System.err);
                if (e instanceof WrongTokenException) {
                    StackTraceElement[] trace = e.getStackTrace();
                    if (trace[0].getMethodName().equals("parsePrimaryExp")
                            && trace[1].getMethodName().equals("parseBaseUnaryExp")
                            && trace[2].getMethodName().equals("parseUnaryExp")) {
                        throw new AssertionError("panic");
                    }
//                    if (trace[0].getClassName().equals("frontend.syntax.expr.ExprParser")) {
//                        throw new AssertionError("panic");
//                    }
//                    System.err.println(trace[0].getClassName());
                }
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

    public Mips getSpecialMips() {
        if (SpecialOptimize.ENABLE_SPECIAL_OPTIMIZE) {
            // Hash Token List
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            tokens.output(new PrintStream(stream));
            String tokenize = stream.toString();
            if (MathUtil.encrypt(tokenize).equals(SpecialOptimize.getPattern())) {
                return SpecialOptimize.specialGenerate();
            }
            return null;
        } else {
            throw new AssertionError("Special optimize not allowed");
        }
    }

}
