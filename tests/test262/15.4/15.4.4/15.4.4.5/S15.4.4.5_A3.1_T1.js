  var x = new Array(0, 1, 2, 3);
  {
    var __result1 = x.join("") !== "0123";
    var __expect1 = false;
  }
  x = new Array(0, 1, 2, 3);
  {
    var __result2 = x.join("\\") !== "0\\1\\2\\3";
    var __expect2 = false;
  }
  {
    var __result3 = x.join("&") !== "0&1&2&3";
    var __expect3 = false;
  }
  {
    var __result4 = x.join(true) !== "0true1true2true3";
    var __expect4 = false;
  }
  {
    var __result5 = x.join(Infinity) !== "0Infinity1Infinity2Infinity3";
    var __expect5 = false;
  }
  {
    var __result6 = x.join(null) !== "0null1null2null3";
    var __expect6 = false;
  }
  {
    var __result7 = x.join(undefined) !== "0,1,2,3";
    var __expect7 = false;
  }
  {
    var __result8 = x.join(NaN) !== "0NaN1NaN2NaN3";
    var __expect8 = false;
  }
  