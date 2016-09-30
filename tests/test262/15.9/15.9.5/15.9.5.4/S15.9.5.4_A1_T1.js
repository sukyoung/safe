  x = Date.prototype.toTimeString;
  if (x === 1)
    Date.prototype.toTimeString = 2;
  else
    Date.prototype.toTimeString = 1;
  {
    var __result1 = Date.prototype.toTimeString === x;
    var __expect1 = false;
  }
  