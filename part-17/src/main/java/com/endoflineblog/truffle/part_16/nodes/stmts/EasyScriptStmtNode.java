package com.endoflineblog.truffle.part_16.nodes.stmts;

import com.endoflineblog.truffle.part_16.nodes.EasyScriptNode;
import com.endoflineblog.truffle.part_16.nodes.root.StmtBlockRootNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.BlockStmtNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.blocks.UserFuncBodyStmtNode;
import com.endoflineblog.truffle.part_16.runtime.debugger.BlockDebuggerScopeObject;
import com.endoflineblog.truffle.part_16.runtime.debugger.FuncDebuggerScopeObject;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Cached.Shared;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.InstrumentableNode;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.NodeLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * The abstract common ancestor of all AST Nodes that represent statements in EasyScript,
 * like declaring a variable or constant.
 * Similar to the class with the same name from part 15,
 * but has a few additions related to debugger support:
 * <ul>
 *     <li>
 *         Implements the {@link InstrumentableNode} interface.
 *         To help with implementing the {@link #createWrapper(ProbeNode)} method of that interface,
 *         we annotate the class with {@link GenerateWrapper}.
 *         We also override the {@link #hasTag(Class)} method to return true for {@link StandardTags.StatementTag}.
 *     </li>
 *     <li>
 *         We store the {@link SourceSection} of the statement in a field,
 *         and return it in the override of the {@link #getSourceSection()} method.
 *         We initialize the field in the constructor,
 *         and each statement subclass must pass a value to it.
 *     </li>
 *     <li>
 *         We add a {@link #findParentBlock()} method that returns the block containing the given statement.
 *         It's used in {@link BlockStmtNode} and {@link BlockDebuggerScopeObject}.
 *     </li>
 *     <li>
 *         We export the {@link NodeLibrary}, and implement the {@link #hasScope} and {@link #getScope} methods from it,
 *         which is needed for the debugger to show the variables in current scope.
 *     </li>
 * </ul>
 */
@GenerateWrapper
@ExportLibrary(value = NodeLibrary.class)
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

    /**
     * Find block of this node. Traverse the parent chain and find the first {@link BlockStmtNode}.
     * If none is found, {@link RootNode} is returned.
     *
     * @return the block node, always non-null. Either {@link BlockStmtNode},
     * {@link UserFuncBodyStmtNode}, or {@link StmtBlockRootNode}
     */
    public final Node findParentBlock() {
        Node parent = this.getParent();
        while (parent != null) {
            if (parent instanceof BlockStmtNode || parent instanceof UserFuncBodyStmtNode) {
                break;
            }
            Node grandParent = parent.getParent();
            if (grandParent == null) {
                // we know that parent is a RootNode here
                // (specifically, a StmtBlockRootNode)
                break;
            }
            parent = grandParent;
        }
        return parent;
    }

    @ExportMessage
    boolean hasScope(
            @SuppressWarnings("unused") Frame frame,
            @Cached(value = "this.findParentBlock()", adopt = false, allowUncached = true) @Shared("thisParentBlock") Node thisParentBlock) {
        return !(thisParentBlock instanceof StmtBlockRootNode);
    }

    /**
     * Create a debugger scope for the current statement.
     * If the statement is placed on the first level of a user-defined function,
     * we return an instance of {@link FuncDebuggerScopeObject} -
     * for statements on the second or lower level of a user-defined function,
     * or for statements inside the main program block, we return an instance of {@link BlockDebuggerScopeObject}.
     */
    @ExportMessage
    Object getScope(
            Frame frame,
            @SuppressWarnings("unused") boolean nodeEnter,
            @Cached(value = "this.findParentBlock()", adopt = false, allowUncached = true) @Shared("thisParentBlock") Node thisParentBlock) {
        return thisParentBlock instanceof BlockStmtNode
                ? new BlockDebuggerScopeObject((BlockStmtNode) thisParentBlock, frame)
                : new FuncDebuggerScopeObject((UserFuncBodyStmtNode) thisParentBlock, frame);
    }
}
