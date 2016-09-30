  {
    var __result1 = Array.prototype.concat.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.concat.length;
  {
    var __result2 = Array.prototype.concat.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.concat.length === undefined;
    var __expect3 = false;
  }
  