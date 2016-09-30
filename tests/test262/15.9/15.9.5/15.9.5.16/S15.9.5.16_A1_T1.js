  x = Date.prototype.getDay;
  if (x === 1)
    Date.prototype.getDay = 2;
  else
    Date.prototype.getDay = 1;
  {
    var __result1 = Date.prototype.getDay === x;
    var __expect1 = false;
  }
  