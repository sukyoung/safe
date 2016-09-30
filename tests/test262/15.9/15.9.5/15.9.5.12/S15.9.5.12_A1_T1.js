  x = Date.prototype.getMonth;
  if (x === 1)
    Date.prototype.getMonth = 2;
  else
    Date.prototype.getMonth = 1;
  {
    var __result1 = Date.prototype.getMonth === x;
    var __expect1 = false;
  }
  