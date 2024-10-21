package com.endoflineblog.truffle.part_15.nodes.stmts;

import com.endoflineblog.truffle.part_15.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.source.SourceSection;

/**
 * The abstract common ancestor of all AST Nodes that represent statements in EasyScript,
 * like declaring a variable or constant.
 * Identical to the class with the same name from part 14.
 */
@GenerateWrapper
public abstract class EasyScriptStmtNode extends EasyScriptNode implements InstrumentableNode {
    private final SourceSection sourceSection;

    protected EasyScriptStmtNode(SourceSection sourceSection) {
        this.sourceSection = sourceSection;
    }

    /**
     * Evaluates this statement, and returns the result of executing it.
     */
    public abstract Object executeStatement(VirtualFrame frame);

    @Override
    public boolean isInstrumentable() {
        return true;
    }

    @Override
    public WrapperNode createWrapper(ProbeNode probe) {
        return new EasyScriptStmtNodeWrapper(this.sourceSection, this, probe);
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == StandardTags.StatementTag.class;
    }

    @Override
    public SourceSection getSourceSection() {
        return this.sourceSection;
    }
}
