  x = Date.prototype.setMonth;
  if (x === 1)
    Date.prototype.setMonth = 2;
  else
    Date.prototype.setMonth = 1;
  {
    var __result1 = Date.prototype.setMonth === x;
    var __expect1 = false;
  }
  