  {
    var __result1 = Array.prototype.toLocaleString.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.toLocaleString.length;
  {
    var __result2 = Array.prototype.toLocaleString.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.toLocaleString.length === undefined;
    var __expect3 = false;
  }
  