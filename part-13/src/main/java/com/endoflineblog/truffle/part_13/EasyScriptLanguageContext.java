package com.endoflineblog.truffle.part_13;

import com.endoflineblog.truffle.part_13.runtime.ClassPrototypeObject;
import com.endoflineblog.truffle.part_13.runtime.GlobalScopeObject;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Identical to the class with the same name from part 11.
 */
public final class EasyScriptLanguageContext {
    private static final TruffleLanguage.ContextReference<EasyScriptLanguageContext> REF =
            TruffleLanguage.ContextReference.create(EasyScriptTruffleLanguage.class);

    /** Retrieve the current language context for the given {@link Node}. */
    public static EasyScriptLanguageContext get(Node node) {
        return REF.get(node);
    }

    public final DynamicObject globalScopeObject;

    /**
     * The object containing the {@code CallTarget}s
     * for the built-in methods of strings.
     */
    public final ClassPrototypeObject stringPrototype;

    public EasyScriptLanguageContext(Shape globalScopeShape, ClassPrototypeObject stringPrototype) {
        this.globalScopeObject = new GlobalScopeObject(globalScopeShape);
        this.stringPrototype = stringPrototype;
    }
}
