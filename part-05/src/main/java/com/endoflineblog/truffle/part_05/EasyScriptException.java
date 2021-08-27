package com.endoflineblog.truffle.part_05;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

/**
 * The exception that's thrown from the EasyScript implementation if any semantic errors are found.
 * Examples of errors are trying to reference a variable that has not been defined,
 * or re-assigning a 'const' variable.
 *
 * Note that exceptions thrown from {@link TruffleLanguage} need to extend the {@link AbstractTruffleException}
 * class in order to not be considered "internal errors" of the language implementation.
 * All other exceptions thrown are considered bugs of the implementation.
 */
public final class EasyScriptException extends AbstractTruffleException {
    public EasyScriptException(String message) {
        this(null, message);
    }

    public EasyScriptException(Node location, String message) {
        super(message, location);
    }
}
