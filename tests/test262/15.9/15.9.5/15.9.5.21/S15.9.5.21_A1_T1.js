  x = Date.prototype.getUTCMinutes;
  if (x === 1)
    Date.prototype.getUTCMinutes = 2;
  else
    Date.prototype.getUTCMinutes = 1;
  {
    var __result1 = Date.prototype.getUTCMinutes === x;
    var __expect1 = false;
  }
  