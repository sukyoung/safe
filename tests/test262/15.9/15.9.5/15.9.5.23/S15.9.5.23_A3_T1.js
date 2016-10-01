  x = Date.prototype.getUTCSeconds.length;
  Date.prototype.getUTCSeconds.length = 1;
  {
    var __result1 = Date.prototype.getUTCSeconds.length !== x;
    var __expect1 = false;
  }
  