  x = Date.prototype.setUTCMilliseconds;
  if (x === 1)
    Date.prototype.setUTCMilliseconds = 2;
  else
    Date.prototype.setUTCMilliseconds = 1;
  {
    var __result1 = Date.prototype.setUTCMilliseconds === x;
    var __expect1 = false;
  }
  