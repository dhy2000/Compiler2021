# 文法解读作业 on SysY 2021

编写的 SysY 测试程序以及对应的标准输入位于 `source` 目录，测试程序文件名为 `testfile*.txt`，对应的输入文件名为 `input*.txt`，输出内容由 gcc 运行得到，未包含在仓库中。

## 运行 testfile

虽然编译课程采用的 SysY 语言是 C 语言的子集，但由于其输入输出函数没有给出实现，因此无法用 gcc 直接编译运行。每次运行前需手动包含 C 标准头文件并添加输入输出函数的声明/定义。

在本仓库中，可以借助位于根目录下的 `Makefile` 对 testfile 进行处理（得到可运行的 C 代码）并调用 gcc 编译执行，从而**直接**运行 SysY 测试程序而无需对 testfile 进行任何手工修改。

- 用于 SysY 的输入函数 `getint` 及输出函数 `printf` 的定义与实现分别位于 `include` 与 `lib` 中。

运行 SysY 程序的命令格式：

```shell
make TestFile=测试程序名 [TestIn=输入文件名] [TestOut=输出文件名]
```

示例：

```shell
make TestFile=source/testfile1.txt TestIn=source/input1.txt TestOut=source/output1.txt
```

若 `TestIn` 或 `TestOut` 缺省，则相应使用标准输入/标准输出。

> 