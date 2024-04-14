package com.endoflineblog.truffle.part_13.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public final class ObjectPrototype extends AbstractClassPrototypeObject {
    public ObjectPrototype(Shape rootShape) {
        super(rootShape, "Object");
    }
}
