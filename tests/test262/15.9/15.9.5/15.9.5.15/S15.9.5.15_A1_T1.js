  x = Date.prototype.getUTCDate;
  if (x === 1)
    Date.prototype.getUTCDate = 2;
  else
    Date.prototype.getUTCDate = 1;
  {
    var __result1 = Date.prototype.getUTCDate === x;
    var __expect1 = false;
  }
  