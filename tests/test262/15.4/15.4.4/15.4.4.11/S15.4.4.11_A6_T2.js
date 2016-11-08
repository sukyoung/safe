  Array.prototype[1] = - 1;
  var x = [1, 0, ];
  x.length = 2;
  x.sort();
  {
    var __result1 = x[0] !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = x[1] !== 1;
    var __expect2 = false;
  }
  x.length = 0;
  {
    var __result3 = x[0] !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = x[1] !== - 1;
    var __expect4 = false;
  }
  Object.prototype[1] = - 1;
  Object.prototype.length = 2;
  Object.prototype.sort = Array.prototype.sort;
  x = {
    0 : 1,
    1 : 0
  };
  x.sort();
  {
    var __result5 = x[0] !== 0;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== 1;
    var __expect6 = false;
  }
  delete x[0];
  delete x[1];
  {
    var __result7 = x[0] !== undefined;
    var __expect7 = false;
  }
  {
    var __result8 = x[1] !== - 1;
    var __expect8 = false;
  }
  