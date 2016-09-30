  {
    var __result1 = Array.prototype.toString.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.toString.length;
  {
    var __result2 = Array.prototype.toString.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.toString.length === undefined;
    var __expect3 = false;
  }
  