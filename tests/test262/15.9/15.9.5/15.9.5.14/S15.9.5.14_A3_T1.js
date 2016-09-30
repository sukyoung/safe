  x = Date.prototype.getDate.length;
  Date.prototype.getDate.length = 1;
  {
    var __result1 = Date.prototype.getDate.length !== x;
    var __expect1 = false;
  }
  