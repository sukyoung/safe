  x = Date.prototype.getMinutes;
  if (x === 1)
    Date.prototype.getMinutes = 2;
  else
    Date.prototype.getMinutes = 1;
  {
    var __result1 = Date.prototype.getMinutes === x;
    var __expect1 = false;
  }
  