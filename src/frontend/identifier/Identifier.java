package frontend.identifier;

import frontend.syntax.node.ActionScope;

import java.util.Objects;

/**
 * 标识符类
 */
public class Identifier {
    private final String name;  // 名称
    private final ActionScope scope;    // 作用域
    private final Type type;    // 类型
    private final boolean constant; // 是否为常量

    public Identifier(String name, ActionScope scope, Type type, boolean constant) {
        this.name = name;
        this.scope = scope;
        this.type = type;
        this.constant = constant;
    }

    public String getName() {
        return name;
    }

    public ActionScope getScope() {
        return scope;
    }

    public Type getType() {
        return type;
    }

    public boolean isConstant() {
        return constant;
    }

    // 同一作用域下一个变量名只能有一个类型, 常量和非常量不同时出现
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Identifier that = (Identifier) o;
        return name.equals(that.name) && scope.equals(that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, scope);
    }

    public enum Type {
        FUNCTION,
        VARIABLE,
        ARRAY_LIST,
        ARRAY_SQUARE
    }
}
