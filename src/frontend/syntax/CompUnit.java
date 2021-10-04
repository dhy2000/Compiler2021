package frontend.syntax;

import frontend.syntax.decl.Decl;
import frontend.syntax.func.FuncDef;
import frontend.syntax.func.MainFuncDef;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class CompUnit implements Component {

    private final List<Decl> globalVars;
    private final List<FuncDef> functions;
    private final MainFuncDef mainFunc;

    public CompUnit(List<Decl> globalVars, List<FuncDef> functions, MainFuncDef mainFunc) {
        this.globalVars = globalVars;
        this.functions = functions;
        this.mainFunc = mainFunc;
    }

    public Iterator<Decl> iterGlobalVars() {
        return globalVars.listIterator();
    }

    public Iterator<FuncDef> iterFunctions() {
        return functions.listIterator();
    }

    public MainFuncDef getMainFunc() {
        return mainFunc;
    }

    @Override
    public void output(PrintStream ps) {
        globalVars.forEach(decl -> decl.output(ps));
        functions.forEach(funcDef -> funcDef.output(ps));
        mainFunc.output(ps);
        ps.println("<CompUnit>");
    }
}
