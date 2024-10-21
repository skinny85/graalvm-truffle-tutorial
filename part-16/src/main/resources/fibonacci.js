var a = 3;
main();
let b = 4;

function main() {
    var i = 1;
    const results = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
    while (i <= 10) {
        results[i - 1] = fib(i);
        i = i + 1;
    }
    return results;
}

function fib(num) {
    if (num <= 1) {
        return Math.abs(num);
    }
    let n1 = 0, n2 = 1, i = 1;
    while (i < num) {
        const next = n2 + n1;
        n1 = n2;
        n2 = next;
        i = i + 1;
    }
    return n2;
}
