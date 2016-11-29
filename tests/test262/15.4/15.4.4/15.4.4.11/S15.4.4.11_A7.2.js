  {
    var __result1 = Array.prototype.sort.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.sort.length;
  {
    var __result2 = Array.prototype.sort.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.sort.length === undefined;
    var __expect3 = false;
  }
  