package com.endoflineblog.truffle.part_07.nodes.stmts;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.runtime.Undefined;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;

public final class LocalVarDeclStmtNode extends EasyScriptStmtNode {
    public static final Object DUMMY = new Object() {
        @Override
        public String toString() {
            return "Dummy";
        }
    };

    private final FrameSlot frameSlot;

    public LocalVarDeclStmtNode(FrameSlot frameSlot) {
        this.frameSlot = frameSlot;
    }

    @Override
    public Object executeStatement(VirtualFrame frame) {
        frame.setObject(this.frameSlot, this.frameSlot.getInfo() == DeclarationKind.VAR
                // the default value for 'var' is 'undefined'
                ? Undefined.INSTANCE
                // for 'const' and 'let', we write a "dummy" value that LocalVarReferenceExprNode treats specially
                : DUMMY);

        // a definition of a local variable returns undefined,
        // same as a definition of a global variable
        return Undefined.INSTANCE;
    }
}
