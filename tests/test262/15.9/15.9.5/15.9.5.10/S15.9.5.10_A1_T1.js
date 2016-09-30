  x = Date.prototype.getFullYear;
  if (x === 1)
    Date.prototype.getFullYear = 2;
  else
    Date.prototype.getFullYear = 1;
  {
    var __result1 = Date.prototype.getFullYear === x;
    var __expect1 = false;
  }
  