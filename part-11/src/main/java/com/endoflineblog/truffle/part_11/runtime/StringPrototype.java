package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CallTarget;

public final class StringPrototype {
    public final CallTarget charAtMethod;

    public StringPrototype(CallTarget charAtMethod) {
        this.charAtMethod = charAtMethod;
    }
}
