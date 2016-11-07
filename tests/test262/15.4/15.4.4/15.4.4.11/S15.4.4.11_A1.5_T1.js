  var x = new Array(1, 0);
  x.sort();
  {
    var __result1 = x.length !== 2;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 0;
    var __expect2 = false;
  }
/*
  {
    var __result3 = x[1] !== 1;
    var __expect3 = false;
  }
  var x = new Array(1, 0);
  x.sort(undefined);
  {
    var __result4 = x.length !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = x[0] !== 0;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== 1;
    var __expect6 = false;
  }
  */
