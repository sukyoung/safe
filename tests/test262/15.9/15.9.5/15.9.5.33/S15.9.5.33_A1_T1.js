  x = Date.prototype.setUTCMinutes;
  if (x === 1)
    Date.prototype.setUTCMinutes = 2;
  else
    Date.prototype.setUTCMinutes = 1;
  {
    var __result1 = Date.prototype.setUTCMinutes === x;
    var __expect1 = false;
  }
  