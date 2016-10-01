  {
    var __result1 = typeof Number("10") !== "number";
    var __expect1 = false;
  }
  {
    var __result2 = typeof Number(10) !== "number";
    var __expect2 = false;
  }
  {
    var __result3 = typeof Number(new String("10")) !== "number";
    var __expect3 = false;
  }
  {
    var __result4 = typeof Number(new Object(10)) !== "number";
    var __expect4 = false;
  }
  {
    var __result5 = typeof Number("abc") !== "number";
    var __expect5 = false;
  }
  {
    var __result6 = ! isNaN(Number("abc"));
    var __expect6 = false;
  }
  