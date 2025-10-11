package com.endoflineblog.truffle.part_16.nodes.stmts.blocks;

import com.endoflineblog.truffle.part_16.exceptions.ReturnException;
import com.endoflineblog.truffle.part_16.nodes.exprs.functions.ReadClosureArgExprNode;
import com.endoflineblog.truffle.part_16.nodes.stmts.EasyScriptStmtNode;
import com.endoflineblog.truffle.part_16.runtime.Undefined;
import com.endoflineblog.truffle.part_16.runtime.debugger.FuncArgRefObject;
import com.endoflineblog.truffle.part_16.runtime.debugger.RefObject;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.NodeVisitor;
import com.oracle.truffle.api.source.SourceSection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Node for representing the statement blocks of a user-defined function in EasyScript.
 * Returns its value by catching {@link ReturnException}.
 * Similar to the class with the same name from part 15,
 * but has a few additions related to debugger support:
 * <ul>
 *     <li>
 *         It takes a {@link SourceSection} in its constructor,
 *         to pass it to the constructor of its superclass, {@link EasyScriptStmtNode}.
 *     </li>
 *     <li>
 *         We override the {@link #hasTag} method to return {@code true} for {@link StandardTags.RootTag}.
 *     </li>
 *     <li>
 *         We add a method, {@link #getFuncArgAndLocalVarRefs()},
 *         that returns all references to function arguments and local variables in this block.
 *         It's used in {@link com.endoflineblog.truffle.part_16.runtime.debugger.FuncDebuggerScopeObject}.
 *         The results are cached in the {@link #findFuncArgRefsCache} field.
 *     </li>
 * </ul>
 */
public final class UserFuncBodyStmtNode extends EasyScriptStmtNode {
    @Children
    private final EasyScriptStmtNode[] stmts;

    @CompilationFinal(dimensions = 1)
    private RefObject[] findFuncArgRefsCache;

    public UserFuncBodyStmtNode(
            List<EasyScriptStmtNode> stmts, SourceSection sourceSection) {
        super(sourceSection);
        this.stmts = stmts.toArray(new EasyScriptStmtNode[]{});
    }

    /**
     * Evaluating the block statement evaluates all statements inside it,
     * and returns whatever a 'return' statement inside it returns.
     */
    @Override
    @ExplodeLoop
    public Object executeStatement(VirtualFrame frame) {
        for (EasyScriptStmtNode stmt : this.stmts) {
            try {
                stmt.executeStatement(frame);
            } catch (ReturnException e) {
                return e.returnValue;
            }
        }
        // if there was no return statement,
        // then we return 'undefined'
        return Undefined.INSTANCE;
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == StandardTags.RootTag.class;
    }

    public RefObject[] getFuncArgAndLocalVarRefs() {
        if (this.findFuncArgRefsCache == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.findFuncArgRefsCache = this.findFuncArgAndLocalVarRefs();
        }
        return this.findFuncArgRefsCache;
    }

    private RefObject[] findFuncArgAndLocalVarRefs() {
        Set<FuncArgRefObject> funcArgs = new HashSet<>();
        // The first argument is always special - it represents 'this'.
        // We'll never encounter 'this' below, because we check for ReadClosureArgExprNode,
        // while 'this' has its own Node (ThisExprNode)
        funcArgs.add(new FuncArgRefObject("this", null, 0));
        NodeUtil.forEachChild(this, new NodeVisitor() {
            @Override
            public boolean visit(Node visitedNode) {
                if (visitedNode instanceof ReadClosureArgExprNode) {
                    var readClosureArgExprNode = (ReadClosureArgExprNode) visitedNode;
                    funcArgs.add(new FuncArgRefObject(
                            readClosureArgExprNode.argName,
                            readClosureArgExprNode.getSourceSection(),
                            readClosureArgExprNode.argIndex));
                    return true;
                }
                return NodeUtil.forEachChild(visitedNode, this);
            }
        });

        var localVarNodeVisitor = new LocalVarNodeVisitor();
        NodeUtil.forEachChild(this, localVarNodeVisitor);

        var allReferences = new RefObject[funcArgs.size() + localVarNodeVisitor.localVarRefs.size()];
        var i = 0;
        for (var funcArg : funcArgs) {
            allReferences[i++] = funcArg;
        }
        for (var localVar : localVarNodeVisitor.localVarRefs) {
            allReferences[i++] = localVar;
        }
        return allReferences;
    }
}
