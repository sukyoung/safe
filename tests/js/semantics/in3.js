function Class1() {this.x = 1;}
function Class2() {};
Class2.prototype = new Class1();

var o = new Class2();

var __result1 = "x" in o; 
var __expect1 = true;
