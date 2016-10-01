  x = Date.prototype.setUTCFullYear;
  if (x === 1)
    Date.prototype.setUTCFullYear = 2;
  else
    Date.prototype.setUTCFullYear = 1;
  {
    var __result1 = Date.prototype.setUTCFullYear === x;
    var __expect1 = false;
  }
  