  x = Date.prototype.getHours;
  if (x === 1)
    Date.prototype.getHours = 2;
  else
    Date.prototype.getHours = 1;
  {
    var __result1 = Date.prototype.getHours === x;
    var __expect1 = false;
  }
  