package com.endoflineblog.truffle.part_06.runtime;

import com.oracle.truffle.api.CallTarget;

public final class FunctionObject /* implements TruffleObject */ {
    public final CallTarget callTarget;

    public FunctionObject(CallTarget callTarget) {
        this.callTarget = callTarget;
    }
}
