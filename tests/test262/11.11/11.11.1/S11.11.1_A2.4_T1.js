  var x = false;
  {
    var __result1 = ((x = true) && x) !== true;
    var __expect1 = false;
  }
  var x = false;
  {
    var __result2 = (x && (x = true)) !== false;
    var __expect2 = false;
  }
  