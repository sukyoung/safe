  var x = 0.000000000000001;
  {
    var __result1 = Math.floor(x) !== + 0;
    var __expect1 = false;
  }
  var x = 0.999999999999999;
  {
    var __result2 = Math.floor(x) !== + 0;
    var __expect2 = false;
  }
  var x = 0.5;
  {
    var __result3 = Math.floor(x) !== + 0;
    var __expect3 = false;
  }
  