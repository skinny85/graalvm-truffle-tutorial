package com.endoflineblog.truffle.part_16.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A specialized {@link JavaScriptObject} that represent an instance of an
 * {@code Error} class (or subclass)
 * that is used for raising instances of {@code Error} for built-in error conditions,
 * like referencing a property of {@code undefined}.
 * Identical to the class with the same name from part 16.
 */
public final class ErrorJavaScriptObject extends JavaScriptObject {
    public final String name, message;

    public ErrorJavaScriptObject(String name, String message,
            Shape shape, ClassPrototypeObject prototype) {
        super(shape, prototype);

        this.name = name;
        this.message = message;
        var putNode = DynamicObject.PutNode.getUncached();
        putNode.execute(this, "name", EasyScriptTruffleStrings.fromJavaString(name));
        putNode.execute(this, "message", EasyScriptTruffleStrings.fromJavaString(message));
    }
}
