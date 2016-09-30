  x = Date.prototype.getUTCDate.length;
  Date.prototype.getUTCDate.length = 1;
  {
    var __result1 = Date.prototype.getUTCDate.length !== x;
    var __expect1 = false;
  }
  