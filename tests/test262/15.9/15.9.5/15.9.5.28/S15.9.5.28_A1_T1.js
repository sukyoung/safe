  x = Date.prototype.setMilliseconds;
  if (x === 1)
    Date.prototype.setMilliseconds = 2;
  else
    Date.prototype.setMilliseconds = 1;
  {
    var __result1 = Date.prototype.setMilliseconds === x;
    var __expect1 = false;
  }
  