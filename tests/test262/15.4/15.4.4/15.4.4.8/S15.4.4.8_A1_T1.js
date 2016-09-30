  var x = [];
  var reverse = x.reverse();
  {
    var __result1 = reverse !== x;
    var __expect1 = false;
  }
  x = [];
  x[0] = 1;
  var reverse = x.reverse();
  {
    var __result2 = reverse !== x;
    var __expect2 = false;
  }
  x = new Array(1, 2);
  var reverse = x.reverse();
  {
    var __result3 = reverse !== x;
    var __expect3 = false;
  }
  {
    var __result4 = x[0] !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = x[1] !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = x.length !== 2;
    var __expect6 = false;
  }
  