  var x = [];
  x[0] = undefined;
  {
    var __result1 = x.join() !== "";
    var __expect1 = false;
  }
  x = [];
  x[0] = null;
  {
    var __result2 = x.join() !== "";
    var __expect2 = false;
  }
  x = Array(undefined, 1, null, 3);
  {
    var __result3 = x.join() !== ",1,,3";
    var __expect3 = false;
  }
  