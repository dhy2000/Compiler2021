# 语法分析

语法分析代码位于 `frontend.syntax` 包中，其中 `tree` 子包内为语法树相关的类。

## 语法树建模

语法树采用多种不同的节点类进行表示。为了方便管理编译作业要求的语法分析输出，这里定义一个接口 `Component` 用于输出语法成分。

对于文法的每种成分，建立一种语法树节点类。对于每种语法成分，用属性来存储其组成成分（子节点）。对于存在 "或" 关系的非终结符，对其每个具体方向建立类，并让这些类实现同一个接口。

注：在语法分析中，`<BlockItem>`, `<Decl>`, `<BType>` 三种成分不需要输出。

## 表达式

首先进行表达式的解析（与 OO Unit1 比较接近），表达式的解析同其他语法成分在逻辑上相对独立，因此将其单独拆分出包，以 `expr` 包存储。

与表达式相关的文法如下：

```text
<Exp>           := <AddExp>
<Cond>          := <LOrExp>
<LVal>          := Ident { '[' <Exp> ']' } // 普通变量，一维或二维数组
<PrimaryExp>    := '(' <Exp> ')' | <LVal> | <Number> // 三种情况, 子表达式, 左值, 字面量
<Number>        := IntConst
<UnaryExp>      := <PrimaryExp> | <Ident> '(' [ <FuncRParams> ] ')' | <UnaryOp> <UnaryExp> // PrimaryExp 或者 FunctionCall 或者含有一元运算符
<UnaryOp>       := '+' | '-' | '!'  // '!' 仅能在条件表达式中出现
<FuncRParams>   := <Exp> { ',' <Exp> } 
<MulExp>        := <UnaryExp> | <MulExp> ( '*' | '/' | '%' ) <UnaryExp>
<AddExp>        := <MulExp> | <AddExp> ( '+' | '-' ) <MulExp>
<RelExp>        := <AddExp> | <RelExp> ( '<' | '>' | '<=' | '>=' ) <AddExp>
<EqExp>         := <RelExp> | <EqExp> ( '==' | '!=' ) <RelExp>
<LAndExp>       := <EqExp> | <LAndExp> '&&' <EqExp>
<LOrExp>        := <LAndExp> | <LOrExp> '||' <LAndExp>
```

对于现有的文法, 在**不违反原文法**基础上为了简化存储结构，进行一些修改(以及消除左递归)

```text
<LVal>          := Ident { '[' <Exp> ']' } // public class LVal { ident, class Index, List<Index> }
<PrimaryExp>    := <SubExp> | <LVal> | <Number> // Look forward: '(' :: <SubExp>, <Ident> :: <LVal>, <IntConst> :: <Number>
<SubExp>        := '(' <Exp> ')'
<Number>        := IntConst   // 该节点只存一个 Token
<BasicUnaryExp> := <PrimaryExp> | <FunctionCall>    // 即不包含 <UnaryOp> 的 <UnaryExp>
// <BasicUnaryExp> 需要向前看 2 个符号: Ident '(' :: <FunctionCall>, Ident :: <LVal>, '(' :: <SubExp>, IntConst :: <Number>
<FunctionCall>  := <Ident> '(' [ <FuncRParams ] ')' // 写表达式分析时 <FunctionCall> 的 parser 可以暂时留空
<FuncRParams>   := <Exp> { ',', <Exp> } // List<Exp>
<UnaryExp>      := { <UnaryOp> } <BasicUnaryExp> // List<UnaryOp> 
// ---------- 分割线 ----------
<MulExp>        := <UnaryExp> { ('*' | '/' | '%') <UnaryExp> }    // 消左递归, 转成循环形式
<AddExp>        := <MulExp> { ('+' | '-') <MulExp> }
<RelExp>        := <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
<EqExp>         := <RelExp> { ('==' | '!=') <RelExp> }
<LAndExp>       := <EqExp> { '&&' <EqExp> }
<LOrExp>        := <LAndExp> { '||' <LAndExp> }

<Exp>           := <AddExp> // public class Exp extends AddExp { /* Nothing */ } 或者 AddExp parseExp()
<Cond>          := <LOrExp> // public class Cond extends LOrExp { /* Nothing */ } 或者 LOrExp parseCond()
```

对于分割线后面的几种成分（`MulExp`, `AddExp`, `RelExp`, `EqExp`, `LAndExp`, `LOrExp`)，其共同特点为由结构相同的左递归文法改成的，连续的从左向右结合的二元表达式，可以根据它们的共同特性提取出一个抽象的父类，基本结构如下：

```java
import frontend.lexical.token.Token;

public abstract class MultiExp<T> { // T: 当前类低一个层次，例如 AddExp 的 T 为 MulExp
    private final T first;
    private final List<Token> operators;
    private final List<T> operands;
}
```

而以上几类具体的表达式除了自身种类，运算符种类和子节点种类不同以外，结构是很相近的（包括其输出方式），因此将它们的输出方法也在 `MultiExp` 抽象类中默认实现：

```java
import frontend.syntax.tree.Component;

import java.io.PrintStream;

public abstract class MultiExp<T extends Component> implements Component {
    private final String name;

    @Override
    public void output(PrintStream ps) {
        // 输出其子节点
        ps.println(name);
    }
}
```

处理完以上成分之后还有两个 `<Exp>` 和 `<Cond>`，这两种节点只是在 `<AddExp>` 和 `<LOrExp>` 上套了一层，为了输出方便还是创建相应的类，持有其套的类的实例并重写输出方法。

另一方面，对于分割线以上的语法成分，它们之间没有太多共同点，因此针对每种语法成分分别建立类即可。

由上，根据上面的分析，在编写代码时将 `expr` 包内的语法成分分成两大类（也就是拆分成两个包）：一元表达式和多元表达式。其中 `unary` 包含分割线以上的一元表达式，而 `multi` 包含分割线以下的多元表达式。