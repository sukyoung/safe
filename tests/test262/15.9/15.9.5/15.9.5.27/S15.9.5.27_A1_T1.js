  x = Date.prototype.setTime;
  if (x === 1)
    Date.prototype.setTime = 2;
  else
    Date.prototype.setTime = 1;
  {
    var __result1 = Date.prototype.setTime === x;
    var __expect1 = false;
  }
  