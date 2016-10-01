  x = Date.prototype.toDateString.length;
  Date.prototype.toDateString.length = 1;
  {
    var __result1 = Date.prototype.toDateString.length !== x;
    var __expect1 = false;
  }
  