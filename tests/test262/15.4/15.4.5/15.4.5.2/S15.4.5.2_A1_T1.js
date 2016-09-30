  var x = [];
  {
    var __result1 = x.length !== 0;
    var __expect1 = false;
  }
  x[0] = 1;
  {
    var __result2 = x.length !== 1;
    var __expect2 = false;
  }
  x[1] = 1;
  {
    var __result3 = x.length !== 2;
    var __expect3 = false;
  }
  x[2147483648] = 1;
  {
    var __result4 = x.length !== 2147483649;
    var __expect4 = false;
  }
  x[4294967294] = 1;
  {
    var __result5 = x.length !== 4294967295;
    var __expect5 = false;
  }
  