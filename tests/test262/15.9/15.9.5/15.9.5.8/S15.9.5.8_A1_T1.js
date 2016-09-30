  x = Date.prototype.valueOf;
  if (x === 1)
    Date.prototype.valueOf = 2;
  else
    Date.prototype.valueOf = 1;
  {
    var __result1 = Date.prototype.valueOf === x;
    var __expect1 = false;
  }
  