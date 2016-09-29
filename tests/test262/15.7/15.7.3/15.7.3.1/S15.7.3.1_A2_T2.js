  {
    var __result1 = typeof Number.prototype !== "object";
    var __expect1 = false;
  }
  Number.prototype.toString = Object.prototype.toString;
  {
    var __result2 = Number.prototype.toString() !== "[object Number]";
    var __expect2 = false;
  }
  