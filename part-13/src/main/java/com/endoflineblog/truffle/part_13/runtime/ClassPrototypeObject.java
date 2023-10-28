package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link DynamicObject} that represents the prototype of a user-defined class.
 * Each {@link JavaScriptObject instance of a class}
 * points to the prototype of its class,
 * and all property reads of the instance delegate to this prototype object to get a reference to the class' instance method.
 * An instance of this class is created when parsing a class declaration,
 * passed to the {@link com.endoflineblog.truffle.part_13.nodes.exprs.objects.ClassDeclExprNode class declaration Node},
 * and saved as a global variable with the name equal to the name of the class using the
 * {@link com.endoflineblog.truffle.part_13.nodes.stmts.variables.GlobalVarDeclStmtNode}.
 */
@ExportLibrary(InteropLibrary.class)
public final class ClassPrototypeObject extends DynamicObject {
    public final String className;

    public ClassPrototypeObject(Shape shape, String className) {
        super(shape);

        this.className = className;
    }

    @Override
    public String toString() {
        return "[class " + this.className + "]";
    }

    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return this.toString();
    }
}
