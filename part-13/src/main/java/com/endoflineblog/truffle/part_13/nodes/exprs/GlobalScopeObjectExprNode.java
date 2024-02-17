package com.endoflineblog.truffle.part_13.nodes.exprs;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;

/**
 * A simple expression class that returns the
 * {@link com.endoflineblog.truffle.part_13.runtime.GlobalScopeObject global scope object}
 * using the inherited {@link #currentLanguageContext()} method from
 * {@link com.endoflineblog.truffle.part_13.nodes.EasyScriptNode}.
 * Used by classes that access the global scope,
 * like global variable declaration or assignment,
 * so that they can have this Node as a child,
 * and then use the {@link com.oracle.truffle.api.library.CachedLibrary}
 * annotation in their {@link Specialization} methods.
 * Identical to the class with the same name from part 11.
 */
public abstract class GlobalScopeObjectExprNode extends EasyScriptExprNode {
    @Specialization
    protected DynamicObject returnGlobalScopeObject() {
        return this.currentLanguageContext().globalScopeObject;
    }
}
