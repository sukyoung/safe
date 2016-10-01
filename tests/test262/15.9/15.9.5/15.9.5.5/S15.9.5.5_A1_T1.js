  x = Date.prototype.toLocaleString;
  if (x === 1)
    Date.prototype.toLocaleString = 2;
  else
    Date.prototype.toLocaleString = 1;
  {
    var __result1 = Date.prototype.toLocaleString === x;
    var __expect1 = false;
  }
  