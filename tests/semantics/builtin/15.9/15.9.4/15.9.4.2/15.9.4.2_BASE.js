var x = Date.parse;
if(x === 1)
  Date.parse = 2;
else
  Date.parse = 1;

var __result1 = Date.parse === x
var __expect1 = false
