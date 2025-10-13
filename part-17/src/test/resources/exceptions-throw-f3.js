function main() {
    f1();
}
function f1() {
    let x = f2();
    return x;
}
function f2() {
    return f3();
}
function f3() {
    throw 'Exception in f3()';
}
main();
