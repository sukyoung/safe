  x = Date.prototype.getUTCSeconds;
  if (x === 1)
    Date.prototype.getUTCSeconds = 2;
  else
    Date.prototype.getUTCSeconds = 1;
  {
    var __result1 = Date.prototype.getUTCSeconds === x;
    var __expect1 = false;
  }
  