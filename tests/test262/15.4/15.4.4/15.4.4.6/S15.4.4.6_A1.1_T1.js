  var x = new Array();
  var pop = x.pop();
  {
    var __result1 = pop !== undefined;
    var __expect1 = false;
  }
  {
    var __result2 = x.length !== 0;
    var __expect2 = false;
  }
  var x = Array(1, 2, 3);
  x.length = 0;
  var pop = x.pop();
  {
    var __result3 = pop !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = x.length !== 0;
    var __expect4 = false;
  }
  