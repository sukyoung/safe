var count = 0;
//var exmaple = new constructor();

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

