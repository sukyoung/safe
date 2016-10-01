  x = Date.prototype.setUTCHours;
  if (x === 1)
    Date.prototype.setUTCHours = 2;
  else
    Date.prototype.setUTCHours = 1;
  {
    var __result1 = Date.prototype.setUTCHours === x;
    var __expect1 = false;
  }
  