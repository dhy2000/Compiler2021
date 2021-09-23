package com.compiler.process;

import com.compiler.exceptions.StringNotClosedException;
import com.compiler.exceptions.UnrecognizedTokenException;
import com.compiler.model.source.SourceBuffer;
import com.compiler.model.token.*;

import java.util.Objects;

public class Lexer {
    private Lexer() {}

    public static TokenStream tokenize(SourceBuffer source)
            throws UnrecognizedTokenException, StringNotClosedException {
        TokenStream ts = new TokenStream();
        while (!source.reachedEndOfFile()) {
            source.skipBlanks();
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
