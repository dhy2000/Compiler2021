package com.compiler.exceptions;

/**
 * 词法或语法错误
 */
public abstract class SyntaxException extends Exception {

    public SyntaxException() {}

    public SyntaxException(String message) {
        super(message);
    }

}
