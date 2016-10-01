// test 1
var obj1 = {p1:10};
function Class1() {this.p1 = 20};
Class1.prototype = obj1;

var x1 = new Class1();

var __result1 = x1.p1;
var __expect1 = 20;

var __result2 = Object.getPrototypeOf(x1).p1;
var __expect2 = 10;

// test 2
var obj2 = {p1:10};
function Class2() {this.p1 = 20};
Class2.prototype = obj2;

function Class3() {this.p1 = 30};
Class3.prototype = new Class2();

var x2 = new Class3();

var __result1 = x2.p1;
var __expect1 = 30;

var __result2 = Object.getPrototypeOf(x2).p1;
var __expect2 = 20;

var __result3 = Object.getPrototypeOf(Object.getPrototypeOf(x2)).p1;
var __expect3 = 10;
