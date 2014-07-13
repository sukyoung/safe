function qw(arg2) {
  var local = {}
//  arg2.bar();
  return arg2.bar();  // for SAFE
}

var x = {}

x.bar = function() {
  return this.foo();
}

//x.foo = function() {}
x.foo = function() { return 123; }  // for SAFE

//x.bar( );
var __result1 = x.bar( );  // for SAFE
var __expect1 = 123;  // for SAFE

//qw(x );
var __result2 = qw(x );  // for SAFE
var __expect2 = 123;  // for SAFE

//x.foo();
var __result3 = x.foo();  // for SAFE
var __expect3 = 123;  // for SAFE
