  x = Date.prototype.setTime.length;
  Date.prototype.setTime.length = 1;
  {
    var __result1 = Date.prototype.setTime.length !== x;
    var __expect1 = false;
  }
  