  {
    var __result1 = ("x" >= "x ") !== false;
    var __expect1 = false;
  }
  {
    var __result2 = ("" >= "x") !== false;
    var __expect2 = false;
  }
  {
    var __result3 = ("ab" >= "abcd") !== false;
    var __expect3 = false;
  }
  {
    var __result4 = ("abcd" >= "abc\u0064") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = ("x" >= "x" + "y") !== false;
    var __expect5 = false;
  }
  var x = "x";
  {
    var __result6 = (x >= x + "y") !== false;
    var __expect6 = false;
  }
  