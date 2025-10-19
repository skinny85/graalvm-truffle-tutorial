package com.endoflineblog.truffle.part_16.nodes.root;

import com.endoflineblog.truffle.part_16.EasyScriptTruffleLanguage;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.SourceSection;

public final class ProgramRootNode extends RootNode {
    private final Shape rootShape;

    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private DirectCallNode directCallNode;

    public ProgramRootNode(
            EasyScriptTruffleLanguage truffleLanguage,
            Shape rootShape,
            CallTarget callTarget) {
        super(truffleLanguage);

        this.rootShape = rootShape;
        this.directCallNode = DirectCallNode.create(callTarget);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        Environment environment = new Environment(this.rootShape);
        return this.directCallNode.call(Undefined.INSTANCE, environment);
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    protected boolean isInstrumentable() {
        return false;
    }
}
