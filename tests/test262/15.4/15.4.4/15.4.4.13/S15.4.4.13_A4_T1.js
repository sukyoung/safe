  Array.prototype[0] = - 1;
  x = [1, ];
  x.length = 1;
  var unshift = x.unshift(0);
  {
    var __result1 = unshift !== 2;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = x[1] !== 1;
    var __expect3 = false;
  }
  delete x[0];
  {
    var __result4 = x[0] !== - 1;
    var __expect4 = false;
  }
  Object.prototype[0] = - 1;
  Object.prototype.length = 1;
  Object.prototype.unshift = Array.prototype.unshift;
  x = {
    0 : 1
  };
  var unshift = x.unshift(0);
  {
    var __result5 = unshift !== 2;
    var __expect5 = false;
  }
  {
    var __result6 = x[0] !== 0;
    var __expect6 = false;
  }
  {
    var __result7 = x[1] !== 1;
    var __expect7 = false;
  }
  delete x[0];
  {
    var __result8 = x[0] !== - 1;
    var __expect8 = false;
  }
  {
    var __result9 = x.length !== 2;
    var __expect9 = false;
  }
  delete x.length;
  {
    var __result10 = x.length !== 1;
    var __expect10 = false;
  }
  