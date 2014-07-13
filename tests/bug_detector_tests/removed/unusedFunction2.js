var count = 0;
/*
* weird behavior
* 1) var example = new constructor();
*   : exmaple is never used
* 2) constructor();
*   : this yeilds global.
* what?!?!??! 
*/
var example = new constructor();

function unused(a, b) {
  return a+b;
}

function constructor() {
  this.index = counter();
  test(this.index);
  // test(index);   => referenceError occurs, other functions are reported as unused.
}

function test(index) {
  var arg = index + 5;
  call(arg, index, unused);
}

function counter() {
  count = count + 1;
  return count;
}

function call(arg1, arg2, fun) {
  fun(arg1, arg2);  
}

