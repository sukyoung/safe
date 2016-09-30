  x = Date.prototype.toUTCString;
  if (x === 1)
    Date.prototype.toUTCString = 2;
  else
    Date.prototype.toUTCString = 1;
  {
    var __result1 = Date.prototype.toUTCString === x;
    var __expect1 = false;
  }
  