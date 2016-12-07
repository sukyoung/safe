var x;
if (@Top) 
	x = undefined;
else if (@Top) 
	x = null;
else if (@Top) 
	x = true;
else if (@Top) 
	x = 1;
else if (@Top)
	x = "str";
else
	x = {};
		
x.p = 1;
	
var __result1 = x.p;
var __expect1 = 1;

var __result2 = x.p;
var __expect2 = undefined;
