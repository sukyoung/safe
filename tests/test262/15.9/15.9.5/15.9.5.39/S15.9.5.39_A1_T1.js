  x = Date.prototype.setUTCMonth;
  if (x === 1)
    Date.prototype.setUTCMonth = 2;
  else
    Date.prototype.setUTCMonth = 1;
  {
    var __result1 = Date.prototype.setUTCMonth === x;
    var __expect1 = false;
  }
  