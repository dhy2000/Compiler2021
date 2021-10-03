# 语法分析

语法分析代码位于 `frontend.syntax` 包中，包括语法树节点类和解析器类。

## 语法树及其节点

语法树采用多种不同的节点类进行表示。为了方便管理编译作业要求的语法分析输出，这里定义一个接口 `Component` 用于输出语法成分。

对于文法的每种成分(非终结符)，建立一种语法树节点类，并用属性来存储其组成成分（词语(终结符, 叶节点)和子节点(非终结符, 非叶节点)）。对于组成成分存在 "或" 关系的非终结符，对其每个具体方向建立类，并让这些类实现同一个接口。

注：在语法分析中，`<BlockItem>`, `<Decl>`, `<BType>` 三种成分不需要输出。

### 表达式

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

处理完以上成分之后还有两个 `<Exp>` 和 `<Cond>`，这两种节点只是在 `<AddExp>` 和 `<LOrExp>` 上套了一层，为了输出方便还是创建相应的类，持有其套的类的实例并重写输出方法。对于 `ConstExp` ，将其作为 `Exp` 的子类。

另一方面，对于分割线以上的语法成分，它们之间没有太多共同点，因此针对每种语法成分分别建立类即可。

由上，根据上面的分析，在编写代码时将 `expr` 包内的语法成分分成两大类（也就是拆分成两个包）：一元表达式和多元表达式。其中 `unary` 包含分割线以上的一元表达式，而 `multi` 包含分割线以下的多元表达式。

### 语句


### 变量定义


### 函数定义



## 语法分析器 (`Parser`) 编写

语法分析器 (`Parser`) 采用递归下降的方法编写，同样为了使整个编译器工程更加模块化，根据上一节对语法成分的分类，对每一类语法成分编写一个解析器类，解析器类之间结构高度相似，区别仅在其负责解析的语法成分不尽相同。

对于递归下降解析器，其在解析过程中需不停向前读取词法分析后提取出来的词语列表，因此需要一个读取头 "指针" 指向当前读取词语列表的位置（在面向对象语言中，这个 "指针" 实际就是迭代器）。由于实验给出的文法不同非终结符的 `FIRST` 集是相交的，为了避免理论上可能出现的回溯，在解析过程中需要采取 "向前看" 的方式来实际确定下一步需解析的语法成分。

### 迭代器的选取

由于 Java 中普通的迭代器 `Iterator` 只能单向前进，不能停下取当前位置的值, 在这种情况下取出向前看的符号是一个不可逆的过程，如果向前看的符号数量不确定，则取出符号判断不属于某个分支后无法放回，将会给下面的子解析程序结构造成较大破坏。为了使各语法成分的解析程序相对保持完整，采用支持双向前进的 `ListIterator` 替代普通的 `Iterator`，既可以前进 (`.next()`) 取出符号，也可以倒退 (`.previous()`) 放回符号。（ Java 的迭代器是不具备 C++ 迭代器可以直接以指针解引用运算符取出当前值的性质的，若想只取出一个值向前看一步，则只能先调用 `next` 方法取出值并前进一步，再调用 `previous` 方法倒退一步，回到原来指向的位置）

### 解析器类的结构

不同类别语法成分的解析器代码结构大同小异：一个解析器对象持有对词语列表的 `ListIterator` 双向迭代器，并有若干解析方法，一个方法对应着一种语法成分。外部使用时，传入词语迭代器实例化一个解析器类，而后调用相应成分的解析方法即可得到对应的语法树节点对象。

### 向前看符号的实现

对于需要向前看符号才能确定的语法成分，令其解析方法以参数的形式接受其父成分解析程序中向前看取出的符号，而无需再用迭代器取出一遍。（通常的语法成分解析方法入口是不需要传参的，只需要访问读取头迭代器就可以了。） 但对于 `Exp` 等出现位置较为复杂的语法成分，其可能出现在多种不同的父成分下，不同的父成分解析 `Exp` 需要向前看的符号数量也不同，如果仍然采用先取出符号再传参数的方式，`Exp` 及其子成分的解析程序将会较为混乱，为了保持架构的完整性，这里就需要用上 `ListIterator` 的双向特性，若向前看取出的符号不满足其他分支，而接下来又需要走向一个 `Exp` 分支，则回退读取头迭代器，将向前看的符号 "放回去"，再调用 `parseExp` 进行解析。（这种回退并不等同于回溯，可以理解为受限于 Java 迭代器功能的一种折中选择）
