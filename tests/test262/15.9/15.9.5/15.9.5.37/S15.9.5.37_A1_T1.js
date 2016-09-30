  x = Date.prototype.setUTCDate;
  if (x === 1)
    Date.prototype.setUTCDate = 2;
  else
    Date.prototype.setUTCDate = 1;
  {
    var __result1 = Date.prototype.setUTCDate === x;
    var __expect1 = false;
  }
  