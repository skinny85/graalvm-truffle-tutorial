//'use strict';


// Edge case #4: shadowing variables with functions
var ff = 5; // a 'let' here causes an error
function ff(a) {
  return 123;
}
console.log(ff()); // fails with 'ff (!) is not a function' error


// Edge case #5: shadowing variables in function definitions
//function f(a) {
//  let a = 22;
////  var a = 33;
////  var a = 44;
//  return a;
//}
//console.log(f(11));


// Edge case #6: referencing an undeclared variable
//a = 1; // this works in loose mode, not in strict mode though
//a; // this fails in both modes


// Edge case #7: duplicate variables
var a = 1;
var a = 2; // this is allowed, even in strict mode
console.log(a); // will write out '2'
//let b = 3;
//let b = 4; // doesn't work, even in loose mode
//let c = 2;
//var c = 1; // also doesn't work
