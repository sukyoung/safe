  {
    var __result1 = - 1 !== - 1;
    var __expect1 = false;
  }
  {
    var __result2 = - (- 1) !== 1;
    var __expect2 = false;
  }
  var x = - 1;
  {
    var __result3 = - x !== 1;
    var __expect3 = false;
  }
  var x = - 1;
  {
    var __result4 = - (- x) !== - 1;
    var __expect4 = false;
  }
  var object = new Object();
  object.prop = 1;
  {
    var __result5 = - object.prop !== - 1;
    var __expect5 = false;
  }
  