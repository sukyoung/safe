  {
    var __result1 = Array.prototype.unshift.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.unshift.length;
  {
    var __result2 = Array.prototype.unshift.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.unshift.length === undefined;
    var __expect3 = false;
  }
  