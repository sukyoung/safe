  var x = true;
  {
    var __result1 = ((x = false) || x) !== false;
    var __expect1 = false;
  }
  var x = true;
  {
    var __result2 = (x || (x = false)) !== true;
    var __expect2 = false;
  }
  