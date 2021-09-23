package compiler.process;

import compiler.exceptions.StringNotClosedException;
import compiler.exceptions.UnrecognizedTokenException;
import compiler.model.source.SourceBuffer;
import compiler.model.token.*;

import java.util.Objects;

public class Lexer {
    private Lexer() {}

    public static TokenStream tokenize(SourceBuffer source)
            throws UnrecognizedTokenException, StringNotClosedException {
        TokenStream ts = new TokenStream();
        while (!source.reachedEndOfFile()) {
            skipBlanksAndComment(source);
            // detect reserved token
            Token nextToken = nextReservedToken(source);
            // detect Ident
            if (Objects.isNull(nextToken)) {
                nextToken = nextIdent(source);
            }
            // detect IntConst
            if (Objects.isNull(nextToken)) {
                nextToken = nextIntConst(source);
            }
            // detect FormatString
            if (Objects.isNull(nextToken)) {
                nextToken = nextFormatString(source);
            }
            if (Objects.nonNull(nextToken)) {
                ts.append(nextToken);
            } else if (!source.reachedEndOfFile()) {
                throw new UnrecognizedTokenException(source.getLineIndex(),
                        source.getColumnIndex(), source.getRemainingLine());
            }
        }
        return ts;
    }

    private static ReservedToken nextReservedToken(SourceBuffer source) {
        for (TokenType tokenType: TokenType.values()) {
            if (!tokenType.isReserved()) { continue; }
            // check whether match
            String token = tokenType.getContent();
            String next = source.followingSeq(token.length());
            if (token.equals(next)) {
                // Exclude similarly identifiers
                if (tokenType.likeIdentifier()) {
                    String nextExtra = source.followingSeq(token.length() + 1);
                    if (nextExtra.length() > token.length()) {
                        char follow = nextExtra.charAt(token.length());
                        if (follow == '_' || Character.isLetterOrDigit(follow)) {
                            // Not a keyword
                            continue;
                        }
                    }
                }
                source.forward(token.length());
                return new ReservedToken(tokenType);
            }
        }
        return null;
    }

    private static Ident nextIdent(SourceBuffer source) {
        if (source.currentChar() == '_' || Character.isAlphabetic(source.currentChar())) {
            StringBuilder sb = new StringBuilder();
            sb.append(source.currentChar());
            source.forward(1);
            while (source.currentChar() == '_' || Character.isLetterOrDigit(source.currentChar())) {
                sb.append(source.currentChar());
                source.forward(1);
            }
            return new Ident(sb.toString());
        } else { return null; }
    }

    private static IntConst nextIntConst(SourceBuffer source) {
        StringBuilder sb = new StringBuilder();
        if (Character.isDigit(source.currentChar())) {
            while (Character.isDigit(source.currentChar())) {   // single '0', continuous '0', exceed int32 range
                sb.append(source.currentChar());
                source.forward(1);
            }
            return new IntConst(sb.toString());
        } else { return null; }
    }

    private static void skipLineComment(SourceBuffer source) {
        if ("//".equals(source.followingSeq(2))) {
            source.toNextLine();
        }
    }

    private static void skipBlockComment(SourceBuffer source) {
        if ("/*".equals(source.followingSeq(2))) {
            while (!source.reachedEndOfFile() && !"*/".equals(source.followingSeq(2))) {
                source.forward(1);
            }
            if ("*/".equals(source.followingSeq(2))) {
                source.forward(2);
            }
        }
    }

    private static void skipBlanksAndComment(SourceBuffer source) {
        int line = source.getLineIndex();
        int col = source.getColumnIndex();
        while (true) {
            source.skipBlanks();
            skipLineComment(source);
            skipBlockComment(source);
            if (source.getLineIndex() == line && source.getColumnIndex() == col) {
                break;
            }
            line = source.getLineIndex();
            col = source.getColumnIndex();
        }
    }

    private static FormatString nextFormatString(SourceBuffer source) throws StringNotClosedException {
        StringBuilder sb = new StringBuilder();
        if (source.currentChar() == '\"') {
            source.forward(1);
            while (!source.reachedEndOfFile() && !source.reachedEndOfLine() && source.currentChar() != '\"') {
                sb.append(source.currentChar());
                source.forward(1);
            }
            if (source.reachedEndOfFile() || source.reachedEndOfLine()) {
                throw new StringNotClosedException(source.getLineIndex(), source.getColumnIndex(), sb.toString());
            }
            source.forward(1);
            return new FormatString(sb.toString());
        } else { return null; }
    }
}
