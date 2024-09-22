let stackBefore, stackAfter;

try {
    const error = new Error('msg');
    stackBefore = error.stack;
    throw error;
} catch (e) {
    stackAfter = e.stack;
}
