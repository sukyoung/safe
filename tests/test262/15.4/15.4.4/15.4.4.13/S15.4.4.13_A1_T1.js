  var x = new Array();
  var unshift = x.unshift(1);
  {
    var __result1 = unshift !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 1;
    var __expect2 = false;
  }
  var unshift = x.unshift();
  {
    var __result3 = unshift !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = x[1] !== undefined;
    var __expect4 = false;
  }
  var unshift = x.unshift(- 1);
  {
    var __result5 = unshift !== 2;
    var __expect5 = false;
  }
  {
    var __result6 = x[0] !== - 1;
    var __expect6 = false;
  }
  {
    var __result7 = x[1] !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = x.length !== 2;
    var __expect8 = false;
  }
  