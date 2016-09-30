  x = Date.prototype.getUTCFullYear;
  if (x === 1)
    Date.prototype.getUTCFullYear = 2;
  else
    Date.prototype.getUTCFullYear = 1;
  {
    var __result1 = Date.prototype.getUTCFullYear === x;
    var __expect1 = false;
  }
  