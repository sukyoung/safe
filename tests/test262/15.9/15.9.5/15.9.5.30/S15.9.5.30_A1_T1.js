  x = Date.prototype.setSeconds;
  if (x === 1)
    Date.prototype.setSeconds = 2;
  else
    Date.prototype.setSeconds = 1;
  {
    var __result1 = Date.prototype.setSeconds === x;
    var __expect1 = false;
  }
  