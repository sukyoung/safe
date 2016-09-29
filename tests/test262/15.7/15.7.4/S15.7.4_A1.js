  {
    var __result1 = typeof Number.prototype !== "object";
    var __expect1 = false;
  }

    var __result1 = Number.prototype != 0;
    var __expect1 = false;

  {
    var __result3 = 1 / Number.prototype != Number.POSITIVE_INFINITY;
    var __expect3 = false;
  }
  delete Number.prototype.toString;
  {
    var __result4 = Number.prototype.toString() !== "[object Number]";
    var __expect4 = false;
  }
  
