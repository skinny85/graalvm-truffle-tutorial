package com.endoflineblog.truffle.part_05;

import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * The exception that's thrown from the EasyScript implementation if any semantic errors are found.
 * Examples of errors are trying to reference a variable that has not been defined,
 * or re-assigning a 'const' variable.
 *
 * Note that exceptions thrown from {@link TruffleLanguage} need to implement the {@link TruffleException}
 * interface in order to not be considered "internal errors" of the language implementation
 * (throwing which basically signifies a bug).
 */
public final class EasyScriptException extends RuntimeException implements TruffleException {
    public EasyScriptException(String message) {
        super(message);
    }

    /**
     * The only abstract method from {@link TruffleException}.
     * All of the errors at this moment are detected before the Truffle AST Node can be created.
     * Because of that, always return {@code null} here.
     */
    @Override
    public Node getLocation() {
        return null;
    }
}
