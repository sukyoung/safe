  var d1 = new Date(Number.NaN);
  {
    var __result1 = (d1.valueOf()).toString()
    var __expect1 = "NaN";
  }
  var d2 = new Date(Infinity);
  {
    var __result2 = (d2.valueOf()).toString();
    var __expect2 = "NaN";
  }
  var d3 = new Date(- Infinity);
  {
    var __result3 = (d3.valueOf()).toString();
    var __expect3 = "NaN";
  }
  var d4 = new Date(0);
  {
    var __result4 = d4.valueOf();
    var __expect4 = 0;
  }
  var d5 = new Date(- 0);
  {
    var __result5 = d5.valueOf();
    var __expect5 = - 0;
  }
