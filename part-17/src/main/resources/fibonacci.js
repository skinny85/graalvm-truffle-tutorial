function fib(unused, num) {
    let n1 = 0, n2 = 1;
    if (num > 1) {
        var i = 1;
        while (i < num) {
            const next = n1 + n2;
            n1 = n2;
            n2 = next;
            i = i + 1;
        }
        return n2;
    } else {
        return Math.abs(num);
    }
}

let fib3;
fib3 = fib("unused", 3);
if (true) {
    while (true) {
        break;
    }
}
