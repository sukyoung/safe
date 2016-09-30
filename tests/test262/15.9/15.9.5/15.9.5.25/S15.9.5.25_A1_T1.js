  x = Date.prototype.getUTCMilliseconds;
  if (x === 1)
    Date.prototype.getUTCMilliseconds = 2;
  else
    Date.prototype.getUTCMilliseconds = 1;
  {
    var __result1 = Date.prototype.getUTCMilliseconds === x;
    var __expect1 = false;
  }
  