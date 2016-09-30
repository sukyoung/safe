  x = Date.prototype.setHours;
  if (x === 1)
    Date.prototype.setHours = 2;
  else
    Date.prototype.setHours = 1;
  {
    var __result1 = Date.prototype.setHours === x;
    var __expect1 = false;
  }
  