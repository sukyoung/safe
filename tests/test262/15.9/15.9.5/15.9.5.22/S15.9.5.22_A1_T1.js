  x = Date.prototype.getSeconds;
  if (x === 1)
    Date.prototype.getSeconds = 2;
  else
    Date.prototype.getSeconds = 1;
  {
    var __result1 = Date.prototype.getSeconds === x;
    var __expect1 = false;
  }
  