  {
    var __result1 = Array.prototype.pop.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.pop.length;
  {
    var __result2 = Array.prototype.pop.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.pop.length === undefined;
    var __expect3 = false;
  }
  