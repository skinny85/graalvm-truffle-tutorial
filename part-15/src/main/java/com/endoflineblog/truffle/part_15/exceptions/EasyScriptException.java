package com.endoflineblog.truffle.part_15.exceptions;

import com.endoflineblog.truffle.part_15.runtime.EasyScriptTruffleStrings;
import com.endoflineblog.truffle.part_15.runtime.ErrorJavaScriptObject;
import com.endoflineblog.truffle.part_15.runtime.JavaScriptObject;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

/**
 * The exception that's thrown from the EasyScript implementation if any semantic errors are found.
 * Very similar to the class with the same name from part 14,
 * the only difference is that in this part,
 * we use this class not only for built-in errors,
 * but also for implementing exceptions in EasyScript -
 * because of that, we add a {@link #value}
 * field that represents the object that was raised in the {@code throw}
 * statement, and caught in the {@code catch} statement.
 * We also add a bunch of constructors used in various exception handling scenarios.
 *
 * @see #EasyScriptException(Object, Object, JavaScriptObject, Node)
 * @see #EasyScriptException(Object, Node)
 * @see #EasyScriptException(ErrorJavaScriptObject, Node)
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
     * @see com.endoflineblog.truffle.part_15.nodes.stmts.exceptions.ThrowStmtNode
     */
    public EasyScriptException(Object name, Object message, JavaScriptObject javaScriptObject, Node node) {
        super(EasyScriptTruffleStrings.toString(name) + ": " + EasyScriptTruffleStrings.toString(message), node);

        this.value = javaScriptObject;
    }

    /**
     * The constructor used in the {@code throw} statement,
     * for the specialization that handles raising anything that is not an object.
     *
     * @see com.endoflineblog.truffle.part_15.nodes.stmts.exceptions.ThrowStmtNode
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
