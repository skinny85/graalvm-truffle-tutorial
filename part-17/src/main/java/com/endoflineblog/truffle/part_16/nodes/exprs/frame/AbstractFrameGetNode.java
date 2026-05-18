package com.endoflineblog.truffle.part_16.nodes.exprs.frame;

import com.endoflineblog.truffle.part_16.nodes.EasyScriptNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * A new hierarchy of Nodes that represents retrieving the correct execution {@link Frame}.
 * This could be the {@link CurrentFrameGetNode current frame},
 * in case of references to variables and arguments local to a given function,
 * or the {@link ParentFrameGetNode parent frame},
 * in case of referencing variables and arguments from parent scopes.
 */
public abstract class AbstractFrameGetNode extends EasyScriptNode {
    public abstract Frame executeFrame(VirtualFrame frame);
}
