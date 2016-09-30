  x = Date.prototype.getTime;
  if (x === 1)
    Date.prototype.getTime = 2;
  else
    Date.prototype.getTime = 1;
  {
    var __result1 = Date.prototype.getTime === x;
    var __expect1 = false;
  }
  