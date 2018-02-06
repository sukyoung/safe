var __result1 = true;
var __result2 = true;
try {
  var __obj = {
    toString : (function ()
    {
      return "1";
    }),
    valueOf : (function ()
    {
      return new Object();
    })
  };
  if (Number(__obj) !== 1) {
    __result1 = false;
  }
}
catch (e) {
  __result2 = false;
}

var __expect1 = true;
var __expect2 = true;
