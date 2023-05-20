package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A Node that represents the declaration of a variable local to a function.
 */
public final class LocalVarDeclStmtNode extends EasyScriptStmtNode {
    public static final Object DUMMY = new Object() {
        @Override
        public String toString() {
            return "Dummy";
        }
    };

    private final int frameSlot;

    public LocalVarDeclStmtNode(int frameSlot) {
        this.frameSlot = frameSlot;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        frame.setObject(this.frameSlot, frame.getFrameDescriptor().getSlotInfo(this.frameSlot) == DeclarationKind.VAR
                // the default value for 'var' is 'undefined'
                ? Undefined.INSTANCE
                // for 'const' and 'let', we write a "dummy" value that LocalVarReferenceExprNode treats specially
                : DUMMY);

        // a definition of a local variable returns undefined,
        // same as a definition of a global variable
        return Undefined.INSTANCE;
    }
}
