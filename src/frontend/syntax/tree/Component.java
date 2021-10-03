package frontend.syntax.tree;

import java.io.PrintStream;

/**
 * 用于管理语法树成分的输出(按照 "语法分析" 作业的要求)
 */
public interface Component {
    void output(PrintStream ps);
}
