  var x = new Array(0, 1, 2, 3);
  {
    var __result1 = x.join(undefined) !== "0,1,2,3";
    var __expect1 = false;
  }
  x = [];
  x[0] = 0;
  x[3] = 3;
  {
    var __result2 = x.join(undefined) !== "0,,,3";
    var __expect2 = false;
  }
  x = [];
  x[0] = 0;
  {
    var __result3 = x.join(undefined) !== "0";
    var __expect3 = false;
  }
  