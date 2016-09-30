  var x = [];
  {
    var __result1 = x.length !== 0;
    var __expect1 = false;
  }
  x[0] = 0;
  var unshift = x.unshift(true, Number.POSITIVE_INFINITY, "NaN", "1", - 1);
  {
    var __result2 = unshift !== 6;
    var __expect2 = false;
  }
  {
    var __result3 = x[5] !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = x[0] !== true;
    var __expect4 = false;
  }
  {
    var __result5 = x[1] !== Number.POSITIVE_INFINITY;
    var __expect5 = false;
  }
  {
    var __result6 = x[2] !== "NaN";
    var __expect6 = false;
  }
  {
    var __result7 = x[3] !== "1";
    var __expect7 = false;
  }
  {
    var __result8 = x[4] !== - 1;
    var __expect8 = false;
  }
  {
    var __result9 = x.length !== 6;
    var __expect9 = false;
  }
  