var x = Date.UTC;
if(x === 1)
  Date.UTC = 2;
else
  Date.UTC = 1;

var __result1 = Date.UTC === x
var __expect1 = false
