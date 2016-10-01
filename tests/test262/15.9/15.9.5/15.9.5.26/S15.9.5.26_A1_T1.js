  x = Date.prototype.getTimezoneOffset;
  if (x === 1)
    Date.prototype.getTimezoneOffset = 2;
  else
    Date.prototype.getTimezoneOffset = 1;
  {
    var __result1 = Date.prototype.getTimezoneOffset === x;
    var __expect1 = false;
  }
  