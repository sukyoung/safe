  {
    var __result1 = Array.hasOwnProperty('length') !== true;
    var __expect1 = false;
  }
  delete Array.length;
  {
    var __result2 = Array.hasOwnProperty('length') !== true;
    var __expect2 = false;
  }
  {
    var __result3 = Array.length === undefined;
    var __expect3 = false;
  }
  