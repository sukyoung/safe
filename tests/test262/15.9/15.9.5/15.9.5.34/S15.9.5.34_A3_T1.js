  x = Date.prototype.setHours.length;
  Date.prototype.setHours.length = 1;
  {
    var __result1 = Date.prototype.setHours.length !== x;
    var __expect1 = false;
  }
  