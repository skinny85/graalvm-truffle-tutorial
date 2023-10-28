package com.endoflineblog.truffle.part_13.common;

import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.oracle.truffle.api.object.Shape;

/**
 * This class holds the {@link Shape}s and {@link ClassPrototypeObject}s
 * that are used by various EasyScript {@link com.oracle.truffle.api.nodes.Node}s.
 * They get access to this class through the
 * {@link com.endoflineblog.truffle.part_13.EasyScriptLanguageContext EasyScript TruffleLanguage context}.
 */
public final class ShapesAndPrototypes {
    public final Shape rootShape;
    public final Shape arrayShape;
    public final ClassPrototypeObject functionPrototype;
    public final ClassPrototypeObject arrayPrototype;
    public final ClassPrototypeObject stringPrototype;

    public ShapesAndPrototypes(Shape rootShape, Shape arrayShape, ClassPrototypeObject functionPrototype,
            ClassPrototypeObject arrayPrototype, ClassPrototypeObject stringPrototype) {
        this.rootShape = rootShape;
        this.arrayShape = arrayShape;
        this.functionPrototype = functionPrototype;
        this.arrayPrototype = arrayPrototype;
        this.stringPrototype = stringPrototype;
    }
}
