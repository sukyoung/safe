  {
    var __result1 = Array.prototype.splice.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.splice.length;
  {
    var __result2 = Array.prototype.splice.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.splice.length === undefined;
    var __expect3 = false;
  }
  