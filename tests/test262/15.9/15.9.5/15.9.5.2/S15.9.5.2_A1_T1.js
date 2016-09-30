  x = Date.prototype.toString;
  if (x === 1)
    Date.prototype.toString = 2;
  else
    Date.prototype.toString = 1;
  {
    var __result1 = Date.prototype.toString === x;
    var __expect1 = false;
  }
  