  x = Date.prototype.getUTCMonth;
  if (x === 1)
    Date.prototype.getUTCMonth = 2;
  else
    Date.prototype.getUTCMonth = 1;
  {
    var __result1 = Date.prototype.getUTCMonth === x;
    var __expect1 = false;
  }
  