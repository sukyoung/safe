  x = Date.prototype.toUTCString.length;
  Date.prototype.toUTCString.length = 1;
  {
    var __result1 = Date.prototype.toUTCString.length !== x;
    var __expect1 = false;
  }
  