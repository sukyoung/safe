function f() {
  if (@Top) aaa = 123;
}
f();

var __result1 = this.aaa;
var __expect1 = 123;

var __result2 = this.aaa;
var __expect2 = undefined;
