function Class(n) {
	this.p = n;
}

for(var i = 4; i; --i) {
	var x = new Class(i) 
}

var __result1 = x.p;
var __expect1 = 1;
