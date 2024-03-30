package com.endoflineblog.truffle.part_13.common;

import com.endoflineblog.truffle.part_13.exceptions.EasyScriptException;

/**
 * An enum that represents the different kinds of variable declarations in JavaScript.
 * Identical to the enum with the same name from part 12.
 */
public enum DeclarationKind {
    /** This represents the 'var' declaration kind. */
    VAR,

    /** This represents the 'let' declaration kind. */
    LET,

    /** This represents the 'const' declaration kind. */
    CONST;

    public static DeclarationKind fromToken(String token) {
        switch (token) {
            case "var": return DeclarationKind.VAR;
            case "let": return DeclarationKind.LET;
            case "const": return DeclarationKind.CONST;
            default: throw new EasyScriptException("Unrecognized variable kind: '" + token + "'");
        }
    }
}
