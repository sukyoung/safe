  var __str = new String(new Array(1, 2, 3));
  {
    var __result1 = typeof __str !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = __str.constructor !== String;
    var __expect2 = false;
  }
  {
    var __result3 = __str != "1,2,3";
    var __expect3 = false;
  }
  