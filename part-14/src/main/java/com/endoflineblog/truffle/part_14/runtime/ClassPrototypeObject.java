package com.endoflineblog.truffle.part_14.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A {@link DynamicObject} that represents the prototype of a class.
 * That can be a user-defined class, or a built-in class like for functions, or arrays
 * (not {@code Object} though, which is represented by {@link ObjectPrototype} -
 * so, this represents the prototype of a class with a parent class).
 * <p>
 * Very similar to the class with the same name from part 13,
 * the only difference is we extend {@link JavaScriptObject}
 * instead of {@link DynamicObject} directly,
 * as these class prototypes have their parent's class prototype,
 * similarly like {@link JavaScriptObject} has a prototype.
 */
@ExportLibrary(InteropLibrary.class)
public class ClassPrototypeObject extends JavaScriptObject {
    public final String className;

    public ClassPrototypeObject(Shape shape, String className, DynamicObject prototype) {
        super(shape, prototype);

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
