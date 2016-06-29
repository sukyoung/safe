var str_top;
for (x in {}) { str_top = x; }
// str_top |-> StringTop

this[str_top] = {};
// #Global |-> @default_other |-> #l [TTT]


var obj1 = { f :function f() {
  var x = 10; // "Writable attribute must be exact for variables in local env." exception.

  // x must be CapturedVar
  return function g() { return x; }
} };

var obj2 = {idfun : function(i) {return i;}};
var e;
for(var c in obj1) {
  // c |-> StringTop
  e = obj1[c];
  obj2[c] = e;
}
obj2.idfun();
var __result1 = obj1.f()();
var __expect1 = 10;

