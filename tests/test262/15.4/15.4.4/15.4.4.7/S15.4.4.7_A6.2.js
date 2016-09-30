  {
    var __result1 = Array.prototype.push.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.prototype.push.length;
  {
    var __result2 = Array.prototype.push.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype.push.length === undefined;
    var __expect3 = false;
  }
  