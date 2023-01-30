package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CallTarget;

public final class StringPrototype {
    public final CallTarget charAtMethod, substringMethod;

    public StringPrototype(CallTarget charAtMethod, CallTarget substringMethod) {
        this.charAtMethod = charAtMethod;
        this.substringMethod = substringMethod;
    }
}
