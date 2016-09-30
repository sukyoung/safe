  {
    var __result1 = typeof Boolean.prototype !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = Boolean.prototype != false;
    var __expect2 = false;
  }
  delete Boolean.prototype.toString;
  {
    var __result3 = Boolean.prototype.toString() !== "[object Boolean]";
    var __expect3 = false;
  }
  