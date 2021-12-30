# Final 代码生成

自增自减。

- `Stmt -> LVal '+' '+' ';'`
- `Stmt -> LVal '-' '-' ';'`

## 解决

- 新增词法 `++`, `--`
- 增加两种语句类型 `IncreaseStmt` 和 `DecreaseStmt`

## !!

- `i ++ 1`