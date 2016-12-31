var i=0;
function f() {i++;}
var __result2;

try { [].reduce(f); } catch(e) { __result2 = e; }

var __result1 = i;
var __expect1 = 0;
var __expect2 = @TypeErr;

