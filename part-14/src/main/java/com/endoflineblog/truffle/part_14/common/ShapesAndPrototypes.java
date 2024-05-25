package com.endoflineblog.truffle.part_14.common;

import com.endoflineblog.truffle.part_14.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_14.runtime.ObjectPrototype;
import com.oracle.truffle.api.object.Shape;

/**
 * This class holds the {@link Shape}s and {@link ClassPrototypeObject}s
 * that are used by various EasyScript {@link com.oracle.truffle.api.nodes.Node}s.
 * They get access to this class through the
 * {@link com.endoflineblog.truffle.part_14.EasyScriptLanguageContext EasyScript TruffleLanguage context}.
 * <p>
 * Very similar to the class with the same name from part 13,
 * the only difference is the added {@link #objectPrototype}
 * field that contains the prototype of {@code Object},
 * which is the root of the class hierarchy in JavaScript.
 */
public final class ShapesAndPrototypes {
    public final Shape rootShape;
    public final Shape arrayShape;
    public final ObjectPrototype objectPrototype;
    public final ClassPrototypeObject functionPrototype;
    public final ClassPrototypeObject arrayPrototype;
    public final ClassPrototypeObject stringPrototype;

    public ShapesAndPrototypes(Shape rootShape, Shape arrayShape,
            ObjectPrototype objectPrototype, ClassPrototypeObject functionPrototype,
            ClassPrototypeObject arrayPrototype, ClassPrototypeObject stringPrototype) {
        this.rootShape = rootShape;
        this.arrayShape = arrayShape;
        this.objectPrototype = objectPrototype;
        this.functionPrototype = functionPrototype;
        this.arrayPrototype = arrayPrototype;
        this.stringPrototype = stringPrototype;
    }
}
