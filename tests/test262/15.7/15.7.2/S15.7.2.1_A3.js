  var x1 = new Number(1);
  {
    var __result1 = x1.valueOf() !== 1;
    var __expect1 = false;
  }
  var x2 = new Number();
  var __result2 = x2.valueOf() !== 0;
  var __expect2 = false;

var __result3 = 1/x2.valueOf() !== Number.POSITIVE_INFINITY
var __expect3 = false
