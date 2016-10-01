  x = Date.prototype.getUTCDay;
  if (x === 1)
    Date.prototype.getUTCDay = 2;
  else
    Date.prototype.getUTCDay = 1;
  {
    var __result1 = Date.prototype.getUTCDay === x;
    var __expect1 = false;
  }
  