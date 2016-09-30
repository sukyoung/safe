  {
    var __result1 = Array.hasOwnProperty('prototype') !== true;
    var __expect1 = false;
  }
  delete Array.prototype;
  {
    var __result2 = Array.hasOwnProperty('prototype') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.prototype === undefined;
    var __expect3 = false;
  }
  