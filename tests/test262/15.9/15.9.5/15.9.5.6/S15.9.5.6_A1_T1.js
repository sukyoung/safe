  x = Date.prototype.toLocaleDateString;
  if (x === 1)
    Date.prototype.toLocaleDateString = 2;
  else
    Date.prototype.toLocaleDateString = 1;
  {
    var __result1 = Date.prototype.toLocaleDateString === x;
    var __expect1 = false;
  }
  