  Object.prototype[1] = - 1;
  Object.prototype.length = 1;
  Object.prototype.push = Array.prototype.push;
  var x = {
    0 : 0
  };
  var push = x.push(1);
  {
    var __result1 = push !== 2;
    var __expect1 = false;
  }
  {
    var __result2 = x.length !== 2;
    var __expect2 = false;
  }
  {
    var __result3 = x[1] !== 1;
    var __expect3 = false;
  }
  delete x[1];
  {
    var __result4 = x[1] !== - 1;
    var __expect4 = false;
  }
  delete x.length;
  {
    var __result5 = x.length !== 1;
    var __expect5 = false;
  }
  