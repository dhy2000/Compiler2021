# 语法分析

语法分析代码位于 `frontend.syntax` 包中，包括语法树节点类和解析器类。

## 语法树及其节点

语法树采用多种不同的节点类进行表示。为了方便管理编译作业要求的语法分析输出，这里定义一个接口 `Component` 用于输出语法成分。

对于文法的每种成分(非终结符)，建立一种语法树节点类，并用属性来存储其组成成分（词语(终结符, 叶节点)和子节点(非终结符, 非叶节点)）。对于组成成分存在 "或" 关系的非终结符，对其每个具体方向建立类，并让这些类实现同一个接口。

由于编译实验设计到的文法条目较多，工程量相较上学期面向对象课程的表达式求导单元是一个飞升，因此为了简化编译器的开发难度，将整个 `SysY` 文法按照不同的逻辑层次进行分类，分别针对每一类依次分析和编码，从而不太痛苦、不丧失很多信心地完成语法分析任务。根据语法成分出现的层次，分为：表达式、语句、变量定义、函数定义四大类。

注：在语法分析中，`<BlockItem>`, `<Decl>`, `<BType>` 三种成分不需要输出。

### 表达式

首先进行表达式的解析（与 OO Unit1 比较接近），表达式作为整个编译文法中较为基础的成分，其解析同其他语法成分也可以相对独立，因此将其作为一类语法成分，在工程代码中对应到 `expr` 子包。

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
<BaseUnaryExp> := <PrimaryExp> | <FunctionCall> // 即不包含 <UnaryOp> 的 <UnaryExp>
// <BaseUnaryExp> 需要向前看 2 个符号: Ident '(' :: <FunctionCall>, Ident :: <LVal>, '(' :: <SubExp>, IntConst :: <Number>
<FunctionCall>  := <Ident> '(' [ <FuncRParams> ] ')'
<FuncRParams>   := <Exp> { ',', <Exp> } // List<Exp>
<UnaryExp>      := { <UnaryOp> } <BaseUnaryExp> // List<UnaryOp>, UnaryOp 包含在 UnaryExp 内部，不单独建类
// ---------- 分割线 ----------
<MulExp>        := <UnaryExp> { ('*' | '/' | '%') <UnaryExp> }    // 消左递归, 转成循环形式
<AddExp>        := <MulExp> { ('+' | '-') <MulExp> }
<RelExp>        := <AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }
<EqExp>         := <RelExp> { ('==' | '!=') <RelExp> }
<LAndExp>       := <EqExp> { '&&' <EqExp> }
<LOrExp>        := <LAndExp> { '||' <LAndExp> }

<Exp>           := <AddExp> // public class Exp extends AddExp { }
<Cond>          := <LOrExp> // public class Cond extends LOrExp { }
<ConstExp>      := <AddExp>
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
import frontend.syntax.Component;

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

处理完以上成分之后还有两个 `<Exp>` 和 `<Cond>`，这两种节点只是在 `<AddExp>` 和 `<LOrExp>` 上套了一层，为了输出方便还是创建相应的类，持有其套的类的实例并重写输出方法。对于 `ConstExp` ，将其作为 `Exp` 的子类。

另一方面，对于分割线以上的语法成分，它们之间没有太多共同点，因此针对每种语法成分分别建立类即可。

由上，根据上面的分析，在编写代码时将 `expr` 包内的语法成分分成两大类（也就是拆分成两个包）：一元表达式和多元表达式。其中 `unary` 包含分割线以上的一元表达式，而 `multi` 包含分割线以下的多元表达式。

### 语句

从表达式更往上一层的语法是语句 `Stmt` 及其相关的成分。语句是 SysY 语言（也是 C 语言）代码中的一个重要的单元。和语句相关的文法如下：

```text
<Stmt> := <LVal> '=' <Exp> ';'
    | [<Exp>] ';'
    | <Block>
    | 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
    | 'while' '(' <Cond> ')' <Stmt>
    | 'break' ';' | 'continue' ';'
    | 'return' [<Exp>] ';'
    | <LVal> '=' 'getint' '(' ')' ';'
    | 'printf' '(' FormatString { ',' <Exp> } ')' ';'

<BlockItem> := <Decl> | <Stmt>  // <Decl> 目前还未分析，相应代码暂时留空，下一节补上

<Block> := '{' { <BlockItem> } '}'
```

这段文法中，`Stmt` 的可选项(分支)较多，且每种语句的含义有很大不同，为了便于在以后的阶段分开处理，因此将 `Stmt` 的每种分支进行命名并作为独立的语法成分拆分出来。文法改写如下：

```text
// 以分号结尾的简单语句
<AssignStmt>    := <LVal> '=' <Exp> // 这些新增非终结符的组成不含分号
<ExpStmt>       := <Exp>            // 空语句放到最后了
<BreakStmt>     := 'break'
<ContinueStmt>  := 'continue'
<ReturnStmt>    := 'return' [<Exp>]
<InputStmt>     := <LVal> '=' 'getint' '(' ')'
<OutputStmt>    := 'printf' '(' FormatString { ',' <Exp> } ')'
<SimpStmt>      := <AssignStmt> | <ExpStmt> | <BreakStmt> | <ContinueStmt> 
    | <ReturnStmt> | <InputStmt> | <OutputStmt> // <SimpStmt> 是以分号结尾的语句(不含分号)的合集
// 复杂的语句
<BranchStmt>    := 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
<LoopStmt>      := 'while' '(' <Cond> ')' <Stmt>
<CplxStmt>      := <BranchStmt> | <LoopStmt> | <Block>

<Stmt>          := ';' | <SimpStmt> ';' | <CplxStmt>    // 将分号放在这里统一处理

<BlockItem>     := <Decl> | <Stmt>

<Block>         := '{' { <BlockItem> } '}'
```

在改写后的 `Stmt` 相关文法中，大多数分支只需要向前看 1 个符号即可够用，但判断 `AssignStmt`, `InputStmt` 和 `ExpStmt` 需要面临 `LVal` 与 `Exp` 的选择，在这里向前看的符号数量是难以确定的。回顾前一节刚刚写完的表达式部分，可以看出 `LVal` 是 `Exp` 的"子集"（即，若一个短语能够由 `LVal` 推导出来，则一定能被 `Exp` 推导），因此在向前看完其他符号后，直接进行一个 `Exp` 的 parse，由于递归下降对语法成分的匹配是贪心的（尽可能多匹配），因此若解析出来的 `Exp` 仅含有一个 `LVal` 则一定是 `LVal`，反之则不是，由此即解决了 `LVal` 和 `Exp` 的区分问题，进而完成语句部分的语法树节点类建模和解析器的编写。

### 变量声明

变量声明 (`Decl`) 作为和 `Stmt` 同级（均属于 `BlockItem`）但有区别（可以位于函数外定义全局变量）的语法成分，可单独分出一类进行建模与解析。

相关的文法如下：

```text
<Decl>          := <ConstDecl> | <VarDecl>
<BType>         := 'int'
// Const
<ConstDecl>     := 'const' <BType> <ConstDef> { ',' <ConstDef> } ';'
<ConstDef>      := Ident { '[' <ConstExp> ']' } '=' <ConstInitVal>
<ConstInitVal>  := <ConstExp> | '{' [ <ConstInitVal> { ',' <ConstInitVal> } ] '}'
// Var
<VarDecl>       := <BType> <VarDef> { ',' <VarDef> } ';'
<VarDef>        := Ident { '[' <ConstExp> ']' } | Ident { '[' <ConstExp> ']' } '=' <InitVal>
<InitVal>       := <Exp> | '{' [ <InitVal> { ',' <InitVal> } ] '}'
```

由于上述文法中，常量声明和非常量声明是类似的，为了减少解析器中可能存在的重复代码，对文法进行改写：

```text
<BType>         := 'int'
<Decl>          := ['const'] <BType> <Def> { ',' <Def> } ';'    // 'const' 修饰若有，则表示常量
<ArrDef>        := { '[' <ConstExp> ']' }   // 如果没有则不是数组
<Def>           := Ident <ArrayDef> [ '=' <InitVal> ]   // 如果是常量声明则必须有
<InitVal>       := <ExpInitVal> | <ArrInitVal>
<ExpInitVal>    := <Exp>
<ArrInitVal>    := '{' [ <InitVal> ] '}'    // 语义分析时要求必须个数与维度对应
```

改写后的文法相较原始的文法，将常量与变量进行了一些统一，同时将变量的初值进行了一定的层次化处理。

由于改写的文法需要保证和原来的文法等价（至少要保证语法分析作业的输出是一致的，并且语义不能变），因此仍然需要对是否为常量进行区分，而区分的方式采用在相应语法节点中添加一个是否要求是常量的 `boolean` 属性，并在解析器的相应方法中通过传参来传达该信息。在进行输出和语义分析时，只需查看当前节点是否有常量标记即可区分是 `Const` 还是 `Var`。

### 函数定义

行百里者半九十，完成了前面大约四分之三的工作量后，到了最后的一组文法了。函数相关的文法如下：

```text
<FuncDef>       := <FuncType> Ident '(' [<FuncFParams> ] ')' <Block>
<MainFuncDef>   := 'int' 'main' '(' ')' <Block>
<FuncType>      := 'void' | 'int'
<FuncFParams>   := <FuncFParam> { ',' <FuncFParam> }
<FuncFParam>    := <BType> Ident [ '[' ']' { '[' <ConstExp> ']' } ]
```


终于，完成了上述所有语法成分的解析后，以最终的一条入口文法 `CompUnit` 来宣告语法分析作业进入了尾声：

```text
<CompUnit>      := { <Decl> } { <FuncDef> } <MainFuncDef>
```

## 语法分析器 (`Parser`) 编写

语法分析器 (`Parser`) 采用递归下降的方法编写，同样为了使整个编译器工程更加模块化，根据上一节对语法成分的分类，对每一类语法成分编写一个解析器类，解析器类之间结构高度相似，区别仅在其负责解析的语法成分不尽相同。

对于递归下降解析器，其在解析过程中需不停向前读取词法分析后提取出来的词语列表，因此需要一个读取头 "指针" 指向当前读取词语列表的位置（在面向对象语言中，这个 "指针" 实际就是迭代器）。由于实验给出的文法不同非终结符的 `FIRST` 集是相交的，为了避免理论上可能出现的回溯，在解析过程中需要采取 "向前看" 的方式来实际确定下一步需解析的语法成分。

### 迭代器的选取

由于 Java 中普通的迭代器 `Iterator` 只能单向前进，不能停下取当前位置的值, 在这种情况下取出向前看的符号是一个不可逆的过程，如果向前看的符号数量不确定，则取出符号判断不属于某个分支后无法放回，将会给下面的子解析程序结构造成较大破坏。为了使各语法成分的解析程序相对保持完整，采用支持双向前进的 `ListIterator` 替代普通的 `Iterator`，既可以前进 (`.next()`) 取出符号，也可以倒退 (`.previous()`) 放回符号。（ Java 的迭代器是不具备 C++ 迭代器可以直接以指针解引用运算符取出当前值的性质的，若想只取出一个值向前看一步，则只能先调用 `next` 方法取出值并前进一步，再调用 `previous` 方法倒退一步，回到原来指向的位置）

### 解析器类的结构

不同类别语法成分的解析器代码结构大同小异：一个解析器对象持有对词语列表的 `ListIterator` 双向迭代器，并有若干解析方法，一个方法对应着一种语法成分。外部使用时，传入词语迭代器实例化一个解析器类，而后调用相应成分的解析方法即可得到对应的语法树节点对象。

### 向前看符号的实现

对于需要向前看符号才能确定的语法成分，令其解析方法以参数的形式接受其父成分解析程序中向前看取出的符号，而无需再用迭代器取出一遍。（通常的语法成分解析方法入口是不需要传参的，只需要访问读取头迭代器就可以了。） 但对于 `Exp` 等出现位置较为复杂的语法成分，其可能出现在多种不同的父成分下，不同的父成分解析 `Exp` 需要向前看的符号数量也不同，如果仍然采用先取出符号再传参数的方式，`Exp` 及其子成分的解析程序将会较为混乱，为了保持架构的完整性，这里就需要用上 `ListIterator` 的双向特性，若向前看取出的符号不满足其他分支，而接下来又需要走向一个 `Exp` 分支，则回退读取头迭代器，将向前看的符号 "放回去"，再调用 `parseExp` 进行解析。（这种回退并不等同于回溯，可以理解为受限于 Java 迭代器功能的一种折中选择）
