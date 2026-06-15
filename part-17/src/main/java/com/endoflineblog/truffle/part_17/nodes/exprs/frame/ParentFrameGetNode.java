package com.endoflineblog.truffle.part_17.nodes.exprs.frame;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

/**
 * An implementation of {@link AbstractFrameGetNode}
 * that returns the parent {@link Frame},
 * walking up {@link #frameLevel} levels of nesting.
 * Because of how closures are implemented in
 * {@link com.endoflineblog.truffle.part_17.nodes.exprs.functions.FunctionDispatchNode},
 * we know that the materialized parent frame is kept in the argument with index 1
 * of every closure call.
 * The walk is performed in a single {@link ExplodeLoop @ExplodeLoop}-annotated method,
 * so that the partial evaluator unrolls the loop at compile time -
 * this matches what GraalJS does in {@code ScopeFrameNode.EnclosingFunctionFrameNode}.
 * The cast of each parent frame to {@link MaterializedFrame} uses
 * {@link CompilerDirectives#castExact(Object, Class)},
 * which gives the partial evaluator an exact type and skips the runtime checkcast -
 * this matches what GraalJS does in {@code JSFrameUtil.castMaterializedFrame}.
 * Because {@link MaterializedFrame} is an interface, we cannot pass
 * {@code MaterializedFrame.class} to {@link CompilerDirectives#castExact}
 * (which requires an exact class match);
 * instead, we capture the concrete runtime class once at class-load time -
 * this is the same trick GraalJS uses.
 */
public final class ParentFrameGetNode extends AbstractFrameGetNode {
    private static final Class<? extends MaterializedFrame> MATERIALIZED_FRAME_CLASS =
            Truffle.getRuntime().createMaterializedFrame(new Object[0]).getClass();

    private final int frameLevel;

    public ParentFrameGetNode(int frameLevel) {
        assert frameLevel >= 1;
        this.frameLevel = frameLevel;
    }

    @Override
    @ExplodeLoop
    public Frame executeFrame(VirtualFrame frame) {
        Frame result = frame;
        for (int i = 0; i < this.frameLevel; i++) {
            result = CompilerDirectives.castExact(result.getArguments()[1], MATERIALIZED_FRAME_CLASS);
        }
        return result;
    }
}
