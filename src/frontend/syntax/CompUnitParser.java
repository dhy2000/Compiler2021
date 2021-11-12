package frontend.syntax;

import exception.NoMainFuncException;
import exception.EofException;
import exception.WrongTokenException;
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

    private Token getNextToken() throws EofException {    // extract duplicated code
        ParserUtil.detectEof("<CompUnit>", iterator, maxLineNum);
        return iterator.next();
    }

    // <CompUnit>      := { <Decl> } { <FuncDef> } <MainFuncDef>
    public CompUnit parseCompUnit() throws WrongTokenException, EofException, NoMainFuncException {
        final String syntax = "<CompUnit>";
        Token first = getNextToken();
        Token second = getNextToken();
        Token third = null;
        // parse Decl
        List<Decl> globalVariables = new LinkedList<>();
        while (iterator.hasNext()) {
            // const int
            // int ident ~'('
            if (first.getType().equals(Token.Type.CONSTTK) && second.getType().equals(Token.Type.INTTK)) {
                globalVariables.add(new DeclParser(iterator, maxLineNum).parseDecl(first, second));
            } else if (first.getType().equals(Token.Type.INTTK) && second.getType().equals(Token.Type.IDENFR)) {
                third = getNextToken();
                if (!third.getType().equals(Token.Type.LPARENT)) {
                    iterator.previous();
                    third = null;
                    globalVariables.add(new DeclParser(iterator, maxLineNum).parseDecl(first, second));
                } else {
                    break; // fall through to function
                }
            } else {
                break;
            }
            first = getNextToken();
            second = getNextToken();
        }

        // parse FuncDef
        List<FuncDef> functions = new LinkedList<>();
        MainFuncDef mainFunc = null;
        if (Objects.isNull(third)) {
            third = getNextToken();
        }
        while (iterator.hasNext()) {
            if (first.getType().equals(Token.Type.INTTK) && second.getType().equals(Token.Type.MAINTK) && third.getType().equals(Token.Type.LPARENT)) {
                mainFunc = new FuncParser(iterator, maxLineNum).parseMainFuncDef(first, second, third);
                break;
            } else {
                if (!first.getType().equals(Token.Type.INTTK) && !first.getType().equals(Token.Type.VOIDTK)) {
                    throw new WrongTokenException(first.lineNumber(), "<FuncDef>", first);
                }
                if (!second.getType().equals(Token.Type.IDENFR)) {
                    throw new WrongTokenException(second.lineNumber(), "<FuncDef>", second, Token.Type.IDENFR);
                }
                if (!third.getType().equals(Token.Type.LPARENT)) {
                    throw new WrongTokenException(third.lineNumber(), "<FuncDef>", third, Token.Type.LPARENT);
                }
                functions.add(new FuncParser(iterator, maxLineNum).parseFuncDef(first, (Ident) second, third));
            }
            first = getNextToken();
            second = getNextToken();
            third = getNextToken();
        }

        if (Objects.isNull(mainFunc)) {
            throw new NoMainFuncException(maxLineNum, "No main function");
        }

        return new CompUnit(globalVariables, functions, mainFunc);
    }
}
