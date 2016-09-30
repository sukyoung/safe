  {
    var __result1 = Array.prototype.reverse.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.reverse.length;
  {
    var __result2 = Array.prototype.reverse.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.reverse.length === undefined;
    var __expect3 = false;
  }
  