  {
    var __result1 = Array.prototype.join.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.join.length;
  {
    var __result2 = Array.prototype.join.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.join.length === undefined;
    var __expect3 = false;
  }
  