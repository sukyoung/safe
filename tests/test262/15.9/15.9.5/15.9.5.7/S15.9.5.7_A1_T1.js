  x = Date.prototype.toLocaleTimeString;
  if (x === 1)
    Date.prototype.toLocaleTimeString = 2;
  else
    Date.prototype.toLocaleTimeString = 1;
  {
    var __result1 = Date.prototype.toLocaleTimeString === x;
    var __expect1 = false;
  }
  