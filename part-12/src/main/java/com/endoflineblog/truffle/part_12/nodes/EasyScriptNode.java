package com.endoflineblog.truffle.part_12.nodes;

import com.endoflineblog.truffle.part_12.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_12.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_12.EasyScriptTypeSystem;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all EasyScript AST Truffle Nodes.
 * Identical to the class with the same name from part 10.
 */
@TypeSystemReference(EasyScriptTypeSystem.class)
public abstract class EasyScriptNode extends Node {
    /** Allows retrieving the current Truffle language instance from within a Node. */
    protected final EasyScriptTruffleLanguage currentTruffleLanguage() {
        return EasyScriptTruffleLanguage.get(this);
    }

    /** Allows retrieving the current Truffle language Context from within a Node. */
    protected final EasyScriptLanguageContext currentLanguageContext() {
        return EasyScriptLanguageContext.get(this);
    }
}
