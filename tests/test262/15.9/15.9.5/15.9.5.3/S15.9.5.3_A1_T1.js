  x = Date.prototype.toDateString;
  if (x === 1)
    Date.prototype.toDateString = 2;
  else
    Date.prototype.toDateString = 1;
  {
    var __result1 = Date.prototype.toDateString === x;
    var __expect1 = false;
  }
  