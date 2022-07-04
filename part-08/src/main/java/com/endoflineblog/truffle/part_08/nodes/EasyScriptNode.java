package com.endoflineblog.truffle.part_08.nodes;

import com.endoflineblog.truffle.part_08.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_08.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all EasyScript AST Truffle Nodes.
 * Identical to the class with the same name from part 7.
 */
public abstract class EasyScriptNode extends Node {
    /** Allows retrieving the current language context from within a Node. */
    protected final EasyScriptTruffleLanguage currentTruffleLanguage() {
        return EasyScriptTruffleLanguage.get(this);
    }

    /** Allows retrieving the current Truffle language instance from within a Node. */
    protected final EasyScriptLanguageContext currentLanguageContext() {
        return EasyScriptLanguageContext.get(this);
    }
}
