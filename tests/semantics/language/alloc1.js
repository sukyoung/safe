function Class() {
	this.p = 123;
}
var x = new Class();

var __result1 = x.p;
var __expect1 = 123;

var __result2 = x.pp;
var __expect2 = undefined;
