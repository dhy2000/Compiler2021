import compiler.global.Global;
import compiler.exceptions.StringNotClosedException;
import compiler.exceptions.UnrecognizedTokenException;
import compiler.model.source.SourceBuffer;
import compiler.model.token.TokenStream;
import compiler.process.Lexer;
import compiler.process.Reader;

import java.io.FileNotFoundException;

public class Compiler {

    public static void main(String[] args) {
        // load arguments
        try {
            if (args.length > 0) { Global.loadArgs(args); }
            else { Global.loadArgs(new String[]{"-T", "-i", "testfile.txt", "-o", "output.txt"}); }
        } catch (FileNotFoundException e) {
            System.exit(0);
        }
        // read source Code
        SourceBuffer source = new Reader(Global.getSource()).read();
        // to tokenize
        TokenStream tokenStream = null;
        try {
            tokenStream = Lexer.tokenize(source);
        } catch (UnrecognizedTokenException | StringNotClosedException e) {
            System.err.printf("%s occurred at line %d col %d: %s\n", e.getClass().getSimpleName(), e.getLineNumber(), e.getColumnNumber(), e.getMessage());
            System.exit(0);
        }
        if (Global.hasOperationOutput(Global.Operation.TOKENIZE)) {
            tokenStream.printTo(Global.getTarget());
        }
    }
}
