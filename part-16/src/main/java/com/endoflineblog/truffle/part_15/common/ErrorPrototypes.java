package com.endoflineblog.truffle.part_15.common;

import com.endoflineblog.truffle.part_15.runtime.ClassPrototypeObject;

import java.util.Map;

/**
 * A similar class to {@link ShapesAndPrototypes},
 * but that contains only the prototypes for the JavaScript {@code Error}
 * class, and its subclasses (in EasyScript's case, we only support {@code TypeError}).
 */
public final class ErrorPrototypes {
    public final ClassPrototypeObject errorPrototype;
    public final ClassPrototypeObject typeErrorPrototype;
    public final Map<String, ClassPrototypeObject> allBuiltInErrorClasses;

    public ErrorPrototypes(
            ClassPrototypeObject errorPrototype,
            ClassPrototypeObject typeErrorPrototype) {
        this.errorPrototype = errorPrototype;
        this.typeErrorPrototype = typeErrorPrototype;
        this.allBuiltInErrorClasses = Map.of(
                "Error", errorPrototype,
                "TypeError", typeErrorPrototype
        );
    }
}
