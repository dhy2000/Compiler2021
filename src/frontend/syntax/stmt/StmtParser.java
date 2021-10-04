package frontend.syntax.stmt;

import frontend.lexical.TokenList;
import frontend.lexical.token.Token;
import frontend.syntax.expr.multi.Exp;
import frontend.syntax.expr.unary.LVal;
import frontend.syntax.stmt.complex.*;
import frontend.syntax.stmt.simple.*;

import java.util.ListIterator;

public class StmtParser {
    private final ListIterator<Token> iterator;
    private final int maxLineNum;

    public StmtParser(TokenList tokens) {
        this.iterator = tokens.listIterator();
        this.maxLineNum = tokens.getMaxLineNumber();
    }

    public StmtParser(ListIterator<Token> iterator, int maxLineNum) {
        this.iterator = iterator;
        this.maxLineNum = maxLineNum;
    }

    // <AssignStmt>     := <LVal> '=' <Exp>
    public AssignStmt parseAssignStmt(LVal target, Token assignTk) {
        return null;
    }

    // <ExpStmt>       := <Exp>
    public ExpStmt parseExpStmt(Exp exp) {
        return null;
    }

    // <BreakStmt>     := 'break'
    public BreakStmt parseBreakStmt(Token breakTk) {
        return null;
    }

    // <ContinueStmt>  := 'continue'
    public ContinueStmt parseContinueStmt(Token continueTk) {
        return null;
    }

    // <ReturnStmt>    := 'return' [<Exp>]
    public ReturnStmt parseReturnStmt(Token returnTk) {
        return null;
    }

    // <InputStmt>     := <LVal> '=' 'getint' '(' ')'
    public InputStmt parseInputStmt(LVal target, Token assignTk, Token getIntTk) {
        return null;
    }

    // <OutputStmt>    := 'printf' '(' FormatString { ',' <Exp> } ')'
    public OutputStmt parseOutputStmt(Token printfTk) {
        return null;
    }

    // <SplStmt>      := <AssignStmt> | <ExpStmt> | <BreakStmt> | <ContinueStmt> | <ReturnStmt> | <InputStmt> | <OutputStmt>
    public SplStmt parseSimpleStmt(Token first) {
        return null;
    }

    // <IfStmt>        := 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
    public IfStmt parseIfStmt(Token ifTk) {
        return null;
    }

    // <WhileStmt>     := 'while' '(' <Cond> ')' <Stmt>
    public WhileStmt parseWhileStmt(Token whileTk) {
        return null;
    }

    // <CplStmt>       := <BranchStmt> | <LoopStmt> | <Block>
    public CplStmt parseComplexStmt(Token first) {
        return null;
    }

    // <BlockItem>     := <Decl> | <Stmt>
    public BlockItem parseBlockItem() {
        return null;
    }

    // <Block>         := '{' { <BlockItem> } '}'
    public Block parseBlock(Token leftBrace) {
        return null;
    }

    // <Stmt>          := ';' | <SplStmt> ';' | <CplStmt>
    public Stmt parseStmt() {
        return null;
    }

}
