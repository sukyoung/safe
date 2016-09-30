  x = Date.prototype.setDate.length;
  Date.prototype.setDate.length = 1;
  {
    var __result1 = Date.prototype.setDate.length !== x;
    var __expect1 = false;
  }
  