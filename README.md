# Compiler on SysY 2021

北航《编译技术》课程设计。

## 程序说明

采用 Java 语言编写编译器，根据评测要求，主类(启动入口)为位于 `src` 目录下的 `Compiler.java`，类名为 `Compiler`。

编译器读取的 SysY 语言测试源代码文件为 `testfile.txt`，不同阶段的输出要求不同（本节涉及到的所有文件均相对于当前目录）：

- 词法分析：输出到 `output.txt`
- 语法分析：输出到 `output.txt`
- 错误处理：输出到 `error.txt`
- PCODE 目标代码解释执行：从 `stdin` 中读取输入内容，输出运行结果到 `pcoderesult.txt`
- MIPS 目标代码生成：输出到 `mips.txt`

## 启动参数说明

由于编译实验课程分多个阶段（词法分析、语法分析、错误处理、代码生成），有多种难度目标代码可供选择（ `PCODE`, `MIPS` ），且评测要求对于输入输出的限制较多。

为了便于调试，本编译器支持参数化启动，可以通过参数的形式指定测试代码、标准输入、哪些阶段的结果要输出以及输出的位置。

### 编译器类

核心编译器类为 `compiler.MainCompiler`，构造编译器实例需要传入一个配置类 (`compiler.Config`) 的实例 `config`，该类组织了编译器启动需要的参数。

配置类 `Config` 所组织的参数有：

- 测试代码输入来源: `InputStream source`
- 测试程序输入来源: `InputStream input`
- 编译器各阶段输出目标，阶段为枚举类 `Config.Operation` 的对象，输出目标为 `PrintStream` 类型

编译器输出所分的阶段包括：

- 词法分析: `TOKENIZE`
- 语法分析: `SYNTAX`
- 错误处理: `ERROR`
- 中间代码: `MID_CODE`
- 中间代码解释执行: `VM_RUNNER` (对应 PCODE 解释执行)
- 目标代码: `OBJECT_CODE` (对应 MIPS 汇编代码生成)

除此以外，为了便于调试，编译器还保留了一个特殊的输出阶段 `EXCEPTION`，用于指定编译器运行中发生异常时记录异常信息的输出位置（该选项若未指定，则异常信息输出至 `stderr`）。

详细用法见 `src/compiler/Config.java`。

### 命令行参数

除了在构造编译器实例时以对象方式传入参数，本编译器还支持命令行参数。命令行参数帮助说明如下：

```text
java -jar Compiler.jar -s source_file [-i input_file] [targets]
Targets: 
    -T [output_file] : TOKENIZE
    -S [output_file] : SYNTAX
    -E [output_file] : ERROR
    -M [output_file] : MID_CODE
    -V [output_file] : VM_RUNNER
    -O [output_file] : OBJECT_CODE
    -X [output_file] : EXCEPTION
```

（该说明可调用 `compiler.Config` 类的 `usage` 方法以输出）

### 提交评测

提交评测的编译器是没有命令行参数的，其输入输出应遵循评测要求，因此在编译器启动时若检测到**没有提供**命令行参数，则根据相应的评测要求使用**默认参数**。

不同阶段作业的默认参数：

- 词法分析: `-s testfile.txt -T output.txt`
- 语法分析: `-s testfile.txt -S output.txt`
- 错误处理: `-s testfile.txt -E error.txt`
- PCODE 解释执行: `-s testfile.txt -V pcoderesult.txt`
- MIPS 代码生成: `-s testfile.txt -O mips.txt`

## 集成自动测试

为了便于在课上考试中进行测试与 debug，减少对<del>性能过于垃圾</del>官方评测机资源的浪费，本编译器内集成了自动测试工具，位于 `autotest` 包中。

关于自动测试的选项：

- `Compiler.RUN_AUTOTEST`: 启动自动测试模式，在此模式下启动直接运行自动测试，**提交版本务必保证此选项为 `false`！**
- `Compiler.MIPS_TEST`: 测试选用的目标代码，该选项为 `true` 测试 MIPS 汇编（需要 MARS），否则测试 PCODE 解释执行
- `autotest.TestSet.ROOT_PATH`: 指定自动测试所有用例所在的根目录
- `autotest.TestRunner.STOP_AT_FIRST_WRONG`: 为 `true` 则在第一个出错的测试点停止测试并导出该点内容，便于调试
- `autotest.TestRunner.EXPORT_TESTCASE_PATH`: 导出测试文件的目录（测试 MIPS 时也用此目录存储汇编文件）
- `autotest.TestRunner.MARS_PATH`: MARS 的路径
- `autotest.TestRunner.MIPS_NAME`: 保存 MIPS 汇编的文件名，该参数考试时可不修改

自动测试的测试点保存在测试集类 `TestSet` 中，每个点为一个 `TestCase`，包括测试文件、输入文件、输出文件的路径及文件名。注意: `TestCase` 中表示文件的路径需要用相对路径，参考目录为 `TestSet.ROOT_PATH`（不是编译器的当前工作目录）。

目前 `TestSet` 中已经加入 `2021编译实验-代码生成测试库-1109` 测试库的所有测试文件。**运行自动测试前需先下载好测试库并解压到配置的路径下！**
