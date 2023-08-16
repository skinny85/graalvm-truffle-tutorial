package com.endoflineblog.truffle.part_11;

import com.endoflineblog.truffle.part_11.runtime.GlobalScopeObject;
import com.endoflineblog.truffle.part_11.runtime.StringPrototype;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * The class of the context for the
 * {@link EasyScriptTruffleLanguage TruffleLanguage implementaton in this part of the series}.
 * Almost identical to the class with the same name from part 10,
 * the only difference is that we get an instance of {@link StringPrototype}
 * in the constructor, and save it as a field.
 * That field is then read by the expression Node for reading string properties.
 *
 * @see #stringPrototype
 * @see StringPrototype
 * @see com.endoflineblog.truffle.part_11.nodes.exprs.strings.ReadTruffleStringPropertyNode
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
    public final StringPrototype stringPrototype;

    public EasyScriptLanguageContext(Shape globalScopeShape, StringPrototype stringPrototype) {
        this.globalScopeObject = new GlobalScopeObject(globalScopeShape);
        this.stringPrototype = stringPrototype;
    }
}
