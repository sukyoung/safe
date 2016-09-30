  x = Date.prototype.getDate;
  if (x === 1)
    Date.prototype.getDate = 2;
  else
    Date.prototype.getDate = 1;
  {
    var __result1 = Date.prototype.getDate === x;
    var __expect1 = false;
  }
  