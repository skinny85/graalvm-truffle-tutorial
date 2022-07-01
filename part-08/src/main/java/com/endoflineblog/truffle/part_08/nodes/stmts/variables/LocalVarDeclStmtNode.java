package com.endoflineblog.truffle.part_08.nodes.stmts.variables;

import com.endoflineblog.truffle.part_08.DeclarationKind;
import com.endoflineblog.truffle.part_08.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_08.runtime.Undefined;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
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
        // treat this variable as if it wasn't assigned a value yet,
        // to allow for specializations if its runtime type is 'int', 'double' or 'boolean'
        frame.getFrameDescriptor().setFrameSlotKind(this.frameSlot, FrameSlotKind.Illegal);

        // a definition of a local variable returns undefined,
        // same as a definition of a global variable
        return Undefined.INSTANCE;
    }
}