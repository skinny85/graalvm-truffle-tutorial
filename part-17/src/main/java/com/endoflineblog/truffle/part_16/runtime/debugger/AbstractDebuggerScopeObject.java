package com.endoflineblog.truffle.part_16.runtime.debugger;

import com.endoflineblog.truffle.part_16.EasyScriptTruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import java.util.Objects;

/**
 * The common base class for objects that represent a debugger scope containing all references of a statement block.
 * Extended by {@link FuncDebuggerScopeObject} and {@link BlockDebuggerScopeObject}.
 */
@ExportLibrary(InteropLibrary.class)
abstract class AbstractDebuggerScopeObject implements TruffleObject {
    static int MEMBER_CACHE_LIMIT = 4;

    protected final Frame frame;

    AbstractDebuggerScopeObject(Frame frame) {
        this.frame = frame;
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return EasyScriptTruffleLanguage.class;
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
        RefObject[] references = this.getReferences();
        return new RefObjectsArray(references);
    }

    protected abstract RefObject[] getReferences();

    /* We need this method to satisfy the Truffle DSL validation. */
    @ExportMessage
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        throw new UnsupportedOperationException();
    }

    @ExportMessage(name = "isMemberReadable")
    static final class MemberReadable {
        @Specialization(limit = "MEMBER_CACHE_LIMIT", guards = "cachedMember.equals(member)")
        static boolean isMemberReadableCached(
                @SuppressWarnings("unused") AbstractDebuggerScopeObject receiver,
                @SuppressWarnings("unused") String member,
                @Cached("member") @SuppressWarnings("unused") String cachedMember,
                // We cache the member existence for fast-path access
                @Cached("isMemberReadableUncached(receiver, member)") boolean cachedResult) {
            return cachedResult;
        }

        @Specialization(replaces = "isMemberReadableCached")
        static boolean isMemberReadableUncached(AbstractDebuggerScopeObject receiver, String member) {
            return receiver.hasReferenceCalled(member);
        }
    }

    @ExportMessage(name = "readMember")
    static final class ReadMember {
        @Specialization(limit = "MEMBER_CACHE_LIMIT", guards = "cachedMember.equals(member)")
        static Object readMemberCached(
                AbstractDebuggerScopeObject receiver,
                @SuppressWarnings("unused") String member,
                @Cached("member") String cachedMember,
                // We cache the member's reference for fast-path access
                @Cached("receiver.findReference(member)") RefObject refObject,
                @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary)
                throws UnknownIdentifierException {
            return readMember(receiver, cachedMember, refObject, dynamicObjectLibrary);
        }

        @Specialization(replaces = "readMemberCached")
        @TruffleBoundary
        static Object readMemberUncached(
                AbstractDebuggerScopeObject receiver,
                String member,
                @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary)
                throws UnknownIdentifierException {
            RefObject refObject = receiver.findReference(member);
            return readMember(receiver, member, refObject, dynamicObjectLibrary);
        }

        private static Object readMember(
                AbstractDebuggerScopeObject receiver, String member, RefObject refObject,
                DynamicObjectLibrary dynamicObjectLibrary) throws UnknownIdentifierException {
            if (refObject == null) {
                throw UnknownIdentifierException.create(member);
            }
            return refObject.readReference(receiver.frame, dynamicObjectLibrary);
        }
    }

    @ExportMessage(name = "isMemberModifiable")
    static final class MemberModifiable {
        @Specialization(limit = "MEMBER_CACHE_LIMIT", guards = "cachedMember.equals(member)")
        static boolean isMemberModifiableCached(
                @SuppressWarnings("unused") AbstractDebuggerScopeObject receiver,
                @SuppressWarnings("unused") String member,
                @Cached("member") @SuppressWarnings("unused") String cachedMember,
                // We cache the member existence for fast-path access
                @Cached("isMemberModifiableUncached(receiver, member)") boolean cachedResult) {
            return cachedResult;
        }

        @Specialization(replaces = "isMemberModifiableCached")
        static boolean isMemberModifiableUncached(AbstractDebuggerScopeObject receiver, String member) {
            return receiver.hasReferenceCalled(member);
        }
    }

    @ExportMessage(name = "writeMember")
    static final class WriteMember {
        @Specialization(limit = "MEMBER_CACHE_LIMIT", guards = "cachedMember.equals(member)")
        static void writeMemberCached(
                AbstractDebuggerScopeObject receiver,
                String member,
                Object value,
                @Cached("member") @SuppressWarnings("unused") String cachedMember,
                // We cache the member's reference for fast-path access
                @Cached("receiver.findReference(member)") RefObject refObject,
                @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary)
                throws UnknownIdentifierException {
            writeMember(receiver, member, refObject, value, dynamicObjectLibrary);
        }

        @Specialization(replaces = "writeMemberCached")
        @TruffleBoundary
        static void writeMemberUncached(
                AbstractDebuggerScopeObject receiver,
                String member,
                Object value,
                @CachedLibrary(limit = "2") DynamicObjectLibrary dynamicObjectLibrary)
                throws UnknownIdentifierException {
            RefObject refObject = receiver.findReference(member);
            writeMember(receiver, member, refObject, value, dynamicObjectLibrary);
        }

        private static void writeMember(
                AbstractDebuggerScopeObject receiver, String member, RefObject refObject, Object value,
                DynamicObjectLibrary dynamicObjectLibrary)
                throws UnknownIdentifierException {
            if (refObject == null) {
                throw UnknownIdentifierException.create(member);
            }
            refObject.writeReference(receiver.frame, value, dynamicObjectLibrary);
        }
    }

    @ExportMessage
    boolean isMemberInsertable(@SuppressWarnings("unused") String member) {
        return false;
    }

    private boolean hasReferenceCalled(String member) {
        return this.findReference(member) != null;
    }

    RefObject findReference(String member) {
        RefObject[] refObjects = this.getReferences();
        for (var refObject : refObjects) {
            if (Objects.equals(refObject.refName, member)) {
                return refObject;
            }
        }
        return null;
    }
}
