  {
    var __result1 = Array.prototype.shift.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.shift.length;
  {
    var __result2 = Array.prototype.shift.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.shift.length === undefined;
    var __expect3 = false;
  }
  