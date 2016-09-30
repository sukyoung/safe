  {
    var __result1 = typeof Object.prototype.valueOf !== "function";
    var __expect1 = false;
  }
  var obj = new Object(true);
  {
    var __result2 = typeof obj.valueOf !== "function";
    var __expect2 = false;
  }
  {
    var __result3 = obj.valueOf() !== true;
    var __expect3 = false;
  }
  