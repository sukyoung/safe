  x = Date.prototype.getUTCHours;
  if (x === 1)
    Date.prototype.getUTCHours = 2;
  else
    Date.prototype.getUTCHours = 1;
  {
    var __result1 = Date.prototype.getUTCHours === x;
    var __expect1 = false;
  }
  