  var x = new Array();
  var push = x.push(1);
  {
    var __result1 = push !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 1;
    var __expect2 = false;
  }
  var push = x.push();
  {
    var __result3 = push !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = x[1] !== undefined;
    var __expect4 = false;
  }
  var push = x.push(- 1);
  {
    var __result5 = push !== 2;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== - 1;
    var __expect6 = false;
  }
  {
    var __result7 = x.length !== 2;
    var __expect7 = false;
  }
  