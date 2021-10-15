package com.endoflineblog.truffle.part_06;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

/**
 * The exception that's thrown from the EasyScript implementation if any semantic errors are found.
 * Identical to the class with the same name from part 5.
 */
public final class EasyScriptException extends AbstractTruffleException {
    public EasyScriptException(String message) {
        this(null, message);
    }

    public EasyScriptException(Node location, String message) {
        super(message, location);
    }
}
