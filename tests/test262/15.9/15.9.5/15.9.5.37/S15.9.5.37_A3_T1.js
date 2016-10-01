  x = Date.prototype.setUTCDate.length;
  Date.prototype.setUTCDate.length = 1;
  {
    var __result1 = Date.prototype.setUTCDate.length !== x;
    var __expect1 = false;
  }
  