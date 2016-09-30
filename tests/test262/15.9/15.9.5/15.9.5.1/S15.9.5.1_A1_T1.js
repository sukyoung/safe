  x = Date.prototype.constructor;
  if (x === 1)
    Date.prototype.constructor = 2;
  else
    Date.prototype.constructor = 1;
  {
    var __result1 = Date.prototype.constructor === x;
    var __expect1 = false;
  }
  