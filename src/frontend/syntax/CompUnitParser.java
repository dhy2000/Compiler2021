package frontend.syntax;

import frontend.error.exception.syntax.NoMainFuncException;
import frontend.error.exception.syntax.UnexpectedEofException;
import frontend.error.exception.syntax.UnexpectedTokenException;
import frontend.lexical.TokenList;
import frontend.lexical.token.Ident;
import frontend.lexical.token.Token;
import frontend.syntax.decl.Decl;
import frontend.syntax.decl.DeclParser;
import frontend.syntax.func.FuncDef;
import frontend.syntax.func.FuncParser;
import frontend.syntax.func.MainFuncDef;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class CompUnitParser {

    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public CompUnitParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public CompUnitParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    // <CompUnit>      := { <Decl> } { <FuncDef> } <MainFuncDef>
    public CompUnit parseCompUnit() throws UnexpectedTokenException, UnexpectedEofException, NoMainFuncException {
        final String syntax = "<CompUnit>";
        // parse Decl
        ParserUtil.detectEof(syntax, iterator, maxLineNum);
        Token type = iterator.next();
        if (type.getType().equals(Token.Type.INTTK) || type.getType().equals(Token.Type.VOIDTK)) {
            throw new UnexpectedTokenException(type.lineNumber(), syntax, type);
        }
        ParserUtil.detectEof(syntax, iterator, maxLineNum);
        Token name = iterator.next();
        if (name.getType().equals(Token.Type.IDENFR) || name.getType().equals(Token.Type.MAINTK)) {
            throw new UnexpectedTokenException(name.lineNumber(), syntax, type);
        }
        List<Decl> globalVars = new LinkedList<>();
        while (iterator.hasNext()) {
            Token next = iterator.next();
            if (next.getType().equals(Token.Type.LPARENT)) {
                iterator.previous();
                break; // fall into function
            }
            if (type.getType().equals(Token.Type.VOIDTK)) {
                break; // fall through
            }
            if (name.getType().equals(Token.Type.MAINTK)) {
                break; // fall through
            }
            Decl decl = new DeclParser(iterator, maxLineNum).parseDecl(type, name);
            globalVars.add(decl);
            ParserUtil.detectEof(syntax, iterator, maxLineNum);
            type = iterator.next();
            if (type.getType().equals(Token.Type.INTTK) || type.getType().equals(Token.Type.VOIDTK)) {
                throw new UnexpectedTokenException(type.lineNumber(), syntax, type);
            }
            ParserUtil.detectEof(syntax, iterator, maxLineNum);
            name = iterator.next();
            if (name.getType().equals(Token.Type.IDENFR) || name.getType().equals(Token.Type.MAINTK)) {
                throw new UnexpectedTokenException(name.lineNumber(), syntax, type);
            }
        }
        // parse FuncDef
        List<FuncDef> functions = new LinkedList<>();
        MainFuncDef mainFunc = null;
        while (iterator.hasNext()) {
            Token next = ParserUtil.getSpecifiedToken(Token.Type.LPARENT, syntax, iterator, maxLineNum);
            if (name.getType().equals(Token.Type.MAINTK)) {
                if (type.getType().equals(Token.Type.INTTK)) {
                    mainFunc = new FuncParser(iterator, maxLineNum).parseMainFuncDef(type, name, next);
                    break;
                } else {
                    throw new UnexpectedTokenException(type.lineNumber(), syntax, type, Token.Type.INTTK);
                }
            } else if (name.getType().equals(Token.Type.IDENFR)) {
                functions.add(new FuncParser(iterator, maxLineNum).parseFuncDef(type, (Ident) name, next));
            } else {
                throw new UnexpectedTokenException(name.lineNumber(), syntax, name, Token.Type.IDENFR);
            }
        }
        if (Objects.isNull(mainFunc)) {
            throw new NoMainFuncException(maxLineNum, "No main function");
        }
        return new CompUnit(globalVars, functions, mainFunc);
    }
}
