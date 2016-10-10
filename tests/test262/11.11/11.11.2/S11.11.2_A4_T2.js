  {
    var __result1 = (- 1 || 1) !== - 1;
    var __expect1 = false;
  }
  {
    var __result2 = (1 || new Number(0)) !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = (- 1 || NaN) !== - 1;
    var __expect3 = false;
  }
  var x = new Number(- 1);
  {
    var __result4 = (x || new Number(0)) !== x;
    var __expect4 = false;
  }
  var x = new Number(NaN);
  {
    var __result5 = (x || new Number(1)) !== x;
    var __expect5 = false;
  }
  var x = new Number(0);
  {
    var __result6 = (x || new Number(NaN)) !== x;
    var __expect6 = false;
  }
  