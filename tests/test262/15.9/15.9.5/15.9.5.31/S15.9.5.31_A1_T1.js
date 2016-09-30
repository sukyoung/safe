  x = Date.prototype.setUTCSeconds;
  if (x === 1)
    Date.prototype.setUTCSeconds = 2;
  else
    Date.prototype.setUTCSeconds = 1;
  {
    var __result1 = Date.prototype.setUTCSeconds === x;
    var __expect1 = false;
  }
  