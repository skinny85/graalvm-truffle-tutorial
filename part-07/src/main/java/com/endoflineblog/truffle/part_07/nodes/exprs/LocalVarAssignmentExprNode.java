package com.endoflineblog.truffle.part_07.nodes.exprs;

import com.endoflineblog.truffle.part_07.DeclarationKind;
import com.endoflineblog.truffle.part_07.EasyScriptException;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

@NodeChild("initializerExpr")
@NodeField(name = "frameSlot", type = FrameSlot.class)
@NodeField(name = "initializingAssignment", type = boolean.class)
@ImportStatic(DeclarationKind.class)
public abstract class LocalVarAssignmentExprNode extends EasyScriptExprNode {
    protected abstract FrameSlot getFrameSlot();

    protected DeclarationKind getFrameSlotInfo() {
        return (DeclarationKind) this.getFrameSlot().getInfo();
    }

    @Specialization(guards = {
            "!initializingAssignment",
            "getFrameSlotInfo() == CONST"
    })
    protected Object assignmentToConstant(Object value) {
        throw new EasyScriptException("Assignment to constant variable '" + this.getFrameSlot().getIdentifier() + "'");
    }

    @Specialization
    protected int intAssignment(VirtualFrame frame, int value) {
        FrameSlot frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setFrameSlotKind(frameSlot, FrameSlotKind.Int);
        frame.setInt(frameSlot, value);
        return value;
    }

    @Specialization(replaces = "intAssignment")
    protected double doubleAssignment(VirtualFrame frame, double value) {
        FrameSlot frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setFrameSlotKind(frameSlot, FrameSlotKind.Double);
        frame.setDouble(frameSlot, value);
        return value;
    }

    @Fallback
    protected Object objectAssignment(VirtualFrame frame, Object value) {
        FrameSlot frameSlot = this.getFrameSlot();
        frame.getFrameDescriptor().setFrameSlotKind(frameSlot, FrameSlotKind.Object);
        frame.setObject(frameSlot, value);
        return value;
    }
}
