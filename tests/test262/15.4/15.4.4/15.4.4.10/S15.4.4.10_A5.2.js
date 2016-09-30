  {
    var __result1 = Array.prototype.slice.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.slice.length;
  {
    var __result2 = Array.prototype.slice.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.slice.length === undefined;
    var __expect3 = false;
  }
  