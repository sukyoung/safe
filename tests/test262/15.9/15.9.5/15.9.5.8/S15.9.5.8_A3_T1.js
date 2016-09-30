  x = Date.prototype.valueOf.length;
  Date.prototype.valueOf.length = 1;
  {
    var __result1 = Date.prototype.valueOf.length !== x;
    var __expect1 = false;
  }
  