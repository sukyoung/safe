  x = Date.prototype.getTime.length;
  Date.prototype.getTime.length = 1;
  {
    var __result1 = Date.prototype.getTime.length !== x;
    var __expect1 = false;
  }
  