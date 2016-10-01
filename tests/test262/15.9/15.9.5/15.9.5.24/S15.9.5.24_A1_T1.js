  x = Date.prototype.getMilliseconds;
  if (x === 1)
    Date.prototype.getMilliseconds = 2;
  else
    Date.prototype.getMilliseconds = 1;
  {
    var __result1 = Date.prototype.getMilliseconds === x;
    var __expect1 = false;
  }
  