# 错误处理题目

新增错误处理: 在 `ConstExp` 中本应全部使用常量但出现了变量。

- 新增错误类型 `p`
- 使用的 `<ConstExp>` 并非常量或常量表达式
- 误把变量当成 `<ConstExp>` 的变量所在行号

## 解决

- 修改用于在编译期计算常数的 `CalcUtil` 类
- 新增 `VarAtConstException` 异常, 在 `CalcUtil` 处理常量表达式时若遇到非常量则抛出
- 在 `Visitor` 中使用到 `CalcUtil` 之处捕获 `VarAtConstException` 并做错误处理
- 全局变量数组的初始化，虽然也需要编译期完成求值，但不需要是 `const`
  - 在调用 `CalcUtil` 时新增一个参数表明是否做常量检查

## 样例

已用注释标记出有错误的行。

```c
int k = 2;
int arr[2] = {1,2}; // right
int arr_[k] = {1,2}; // p
const int arr_1[2] = {1,k}; // p

const int const1 = 1, const2 = -100;
int change1;

const int con2 = change1; // p

int change3 = change1; // NOT p

int arr1[const1 + 1] = {1, 2};
int arr2[arr[1]] = {1, 2}; // p

int arr3[5] = {1, 2, 3, arr[0], 5}; // NOT p
const int arr4[6] = {1, 2, const2, arr[1], 5, 6}; // p

int gets1(int arr[][change1],int var2){ // p
   const1 = 999; // h
   change1 = var1 + var2; // c
   return (change1);
}

int fun() {
    return 5;
}

int main(){
   change1 = 10;
   const int cc = change1; // p
   const int arr2[3] = {1, 2, change1}; // p
   const int c2 = cc + 1;
   int a3 = arr3[2];

   k = 6;
   const int arr5[k - 1] = {1, 2, 3, 4, 5}; // p
   const int arr6[3] = {1, func1(), 3};     // p

   arr3[k - 4] = 3;

   printf("Kao Shi Jia You");
   return 0;
}
```

## 考试结果

Pass 4/5

`testfile5` 错误，得分 `80%`，疑似漏报了新增的 `p` 类错误。（课下大概没啥问题，直接提交该点能得分 `20%`）
