  {
    var __result1 = ("-1" || "1") !== "-1";
    var __expect1 = false;
  }
  {
    var __result2 = ("-1" || "x") !== "-1";
    var __expect2 = false;
  }
  var x = new String("-1");
  {
    var __result3 = (x || new String(- 1)) !== x;
    var __expect3 = false;
  }
  var x = new String(NaN);
  {
    var __result4 = (x || new String("1")) !== x;
    var __expect4 = false;
  }
  var x = new String("-x");
  {
    var __result5 = (x || new String("x")) !== x;
    var __expect5 = false;
  }
  var x = new String(0);
  {
    var __result6 = (x || new String(NaN)) !== x;
    var __expect6 = false;
  }
  