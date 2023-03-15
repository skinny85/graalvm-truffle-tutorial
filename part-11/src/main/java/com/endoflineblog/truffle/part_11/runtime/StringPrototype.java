package com.endoflineblog.truffle.part_11.runtime;

import com.oracle.truffle.api.CallTarget;

public final class StringPrototype {
    public final CallTarget substringMethod;

    public StringPrototype(CallTarget substringMethod) {
        this.substringMethod = substringMethod;
    }
}
