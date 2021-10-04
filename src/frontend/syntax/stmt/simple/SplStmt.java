package frontend.syntax.stmt.simple;

import frontend.syntax.stmt.Stmt;

/**
 * 以分号结尾的 (通常在一行以内的) 简单语句 (不包括分号), 包括赋值语句, 表达式语句, break/continue 语句，return 语句，输入输出语句
 * 输出自己时不输出名字，因为完整的 Stmt 需要分号
 */
public interface SplStmt extends Stmt {
}
