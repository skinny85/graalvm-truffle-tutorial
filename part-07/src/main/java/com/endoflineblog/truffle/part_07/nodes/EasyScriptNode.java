package com.endoflineblog.truffle.part_07.nodes;

import com.endoflineblog.truffle.part_07.EasyScriptLanguageContext;
import com.endoflineblog.truffle.part_07.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

/**
 * The abstract common ancestor of all EasyScript AST Truffle Nodes.
 * Very similar to the class with the same name from part 6,
 * the only difference is the new {@link #currentTruffleLanguage()}
 * method that allows retrieving the current {@link EasyScriptTruffleLanguage Truffle language}
 * instance, used in {@link com.endoflineblog.truffle.part_07.nodes.stmts.FuncDeclStmtNode}.
 *
 * @see #currentTruffleLanguage()
 */
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
