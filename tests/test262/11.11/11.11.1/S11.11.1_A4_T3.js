  {
    var __result1 = ("0" && "-1") !== "-1";
    var __expect1 = false;
  }
  {
    var __result2 = ("-1" && "x") !== "x";
    var __expect2 = false;
  }
  var y = new String(- 1);
  {
    var __result3 = (new String("-1") && y) !== y;
    var __expect3 = false;
  }
  var y = new String(NaN);
  {
    var __result4 = (new String("0") && y) !== y;
    var __expect4 = false;
  }
  var y = new String("-x");
  {
    var __result5 = (new String("x") && y) !== y;
    var __expect5 = false;
  }
  var y = new String(- 1);
  {
    var __result6 = (new String(NaN) && y) !== y;
    var __expect6 = false;
  }
  