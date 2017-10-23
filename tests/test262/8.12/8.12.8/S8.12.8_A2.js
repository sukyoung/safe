var __result1 = false;
var __result2 = true;
try {
  var __obj = {
    toString : (function ()
    {
      return new Object();
    }),
    valueOf : (function ()
    {
      return 1;
    })
  };
  if (String(__obj) !== "1") {
    __result1 = true;
  }
}
catch (e) {
  __result2 = false;
}

var __expect1 = true;
var __expect2 = true;