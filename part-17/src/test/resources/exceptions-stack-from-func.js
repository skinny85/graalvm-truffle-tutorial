let stackBefore, stackAfter;
function f() {
    const error = new Error('msg');
    stackBefore = error.stack;
    try {
        throw error;
    } catch (e) {
        stackAfter = e.stack;
    }
}
f();
