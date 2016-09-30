  Object.prototype.myproperty = 1;
  {
    var __result1 = Array.prototype.myproperty !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = Array.prototype.hasOwnProperty('myproperty') !== false;
    var __expect2 = false;
  }
  