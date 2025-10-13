package com.endoflineblog.truffle.part_16.exceptions;

import com.endoflineblog.truffle.part_16.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_16.runtime.ErrorJavaScriptObject;
import com.endoflineblog.truffle.part_16.runtime.JavaScriptObject;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

/**
 * The exception that's thrown from the EasyScript implementation if any semantic errors are found.
 * Identical to the class with the same name from part 15.
 */
public final class EasyScriptException extends AbstractTruffleException {
    public final Object value;

    /**
     * This constructor is used only for indicating built-in errors,
     * like extending a class that doesn't exist.
     */
    public EasyScriptException(String message) {
        this(null, message);
    }

    /**
     * This constructor is used only for indicating built-in errors,
     * like assigning to a global variable that doesn't exist.
     */
    public EasyScriptException(Node location, String message) {
        super(message, location);

        this.value = null;
    }

    /**
     * The constructor used in the {@code throw} statement,
     * for the specialization that handles raising an object.
     *
     * @see com.endoflineblog.truffle.part_16.nodes.stmts.exceptions.ThrowStmtNode
     */
    public EasyScriptException(Object name, Object message, JavaScriptObject javaScriptObject, Node node) {
        super(EasyScriptTruffleStrings.toString(name) + ": " + EasyScriptTruffleStrings.toString(message), node);

        this.value = javaScriptObject;
    }

    /**
     * The constructor used in the {@code throw} statement,
     * for the specialization that handles raising anything that is not an object.
     *
     * @see com.endoflineblog.truffle.part_16.nodes.stmts.exceptions.ThrowStmtNode
     */
    public EasyScriptException(Object value, Node node) {
        super(EasyScriptTruffleStrings.toString(value), node);

        this.value = value;
    }

    /**
     * The constructor used for built-in errors,
     * like referencing a property of {@code undefined}.
     */
    public EasyScriptException(ErrorJavaScriptObject errorJavaScriptObject, Node node) {
        super(errorJavaScriptObject.name + ": " + errorJavaScriptObject.message, node);

        this.value = errorJavaScriptObject;
    }
}
