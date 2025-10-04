package com.endoflineblog.truffle.part_16.nodes.exprs.functions;

import com.endoflineblog.truffle.part_16.runtime.Environment;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public abstract class ReadClosureArgExprNode extends AbstractFuncMemberReadNode {
    public final int index;
    public final String argName;

    protected ReadClosureArgExprNode(int index, String argName) {
        this.index = index;
        this.argName = argName;
    }

    @Specialization
    protected Object readArgFromClosureEnv(
            VirtualFrame frame,
            @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary) {
        // because of the logic in FunctionDispatchNode,
        // we know the environment is always the second argument
        Environment environment = (Environment) frame.getArguments()[1];
        return dynamicObjectLibrary.getOrDefault(environment, this.index, null);
        // ToDo: should the last argument be Undefined.INSTANCE instead of null?
        // check with a benchmark (I think it shouldn't matter, but make sure to confirm)
    }
}
