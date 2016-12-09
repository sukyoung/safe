function Foo() {
  this.x = 10;
}

var con;
if (@Top) 
	con = Foo;
else
	con = 1;

var o = new con();

var __result1 = o.x;
var __expect1 = 10;
