  x = Date.prototype.setMinutes;
  if (x === 1)
    Date.prototype.setMinutes = 2;
  else
    Date.prototype.setMinutes = 1;
  {
    var __result1 = Date.prototype.setMinutes === x;
    var __expect1 = false;
  }
  