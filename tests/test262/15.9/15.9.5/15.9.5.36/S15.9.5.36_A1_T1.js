  x = Date.prototype.setDate;
  if (x === 1)
    Date.prototype.setDate = 2;
  else
    Date.prototype.setDate = 1;
  {
    var __result1 = Date.prototype.setDate === x;
    var __expect1 = false;
  }
  