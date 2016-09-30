  x = Date.prototype.setFullYear;
  if (x === 1)
    Date.prototype.setFullYear = 2;
  else
    Date.prototype.setFullYear = 1;
  {
    var __result1 = Date.prototype.setFullYear === x;
    var __expect1 = false;
  }
  