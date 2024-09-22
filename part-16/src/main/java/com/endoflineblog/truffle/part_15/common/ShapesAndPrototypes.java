package com.endoflineblog.truffle.part_15.common;

import com.endoflineblog.truffle.part_15.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_15.runtime.ObjectPrototype;
import com.endoflineblog.truffle.part_15.EasyScriptLanguageContext;
import com.oracle.truffle.api.object.Shape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the {@link Shape}s and {@link ClassPrototypeObject}s
 * that are used by various EasyScript {@link com.oracle.truffle.api.nodes.Node}s.
 * They get access to this class through the
 * {@link EasyScriptLanguageContext EasyScript TruffleLanguage context}.
 * <p>
 * Very similar to the class with the same name from part 14,
 * the only difference is the added {@link #errorPrototypes}
 * field that contains the prototypes of {@code Error}
 * and its subclasses, and the {@link #allBuiltInClasses}
 * {@link Map} that stores the mapping between built-in class names,
 * and its prototypes, which we add to the global scope object in
 * {@link com.endoflineblog.truffle.part_15.EasyScriptTruffleLanguage},
 * so that they are available to EasyScript code.
 */
public final class ShapesAndPrototypes {
    public final Shape rootShape;
    public final Shape arrayShape;
    public final ObjectPrototype objectPrototype;
    public final ClassPrototypeObject functionPrototype;
    public final ClassPrototypeObject arrayPrototype;
    public final ClassPrototypeObject stringPrototype;
    public final ErrorPrototypes errorPrototypes;
    public final Map<String, ClassPrototypeObject> allBuiltInClasses;

    public ShapesAndPrototypes(Shape rootShape, Shape arrayShape,
            ObjectPrototype objectPrototype, ClassPrototypeObject functionPrototype,
            ClassPrototypeObject arrayPrototype, ClassPrototypeObject stringPrototype,
            ErrorPrototypes errorPrototypes) {
        this.rootShape = rootShape;
        this.arrayShape = arrayShape;
        this.objectPrototype = objectPrototype;
        this.functionPrototype = functionPrototype;
        this.arrayPrototype = arrayPrototype;
        this.stringPrototype = stringPrototype;
        this.errorPrototypes = errorPrototypes;

        Map<String, ClassPrototypeObject> allBuiltInClasses = new HashMap<>();
        allBuiltInClasses.put("Object", objectPrototype);
        allBuiltInClasses.putAll(errorPrototypes.allBuiltInErrorClasses);
        this.allBuiltInClasses = Collections.unmodifiableMap(allBuiltInClasses);
    }
}
