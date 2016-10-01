  Array.prototype[1] = - 1;
  var x = [0, 1, ];
  x.length = 2;
  var shift = x.shift();
  {
    var __result1 = shift !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = x[0] !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = x[1] !== - 1;
    var __expect3 = false;
  }
  Object.prototype[1] = - 1;
  Object.prototype.length = 2;
  Object.prototype.shift = Array.prototype.shift;
  x = {
    0 : 0,
    1 : 1
  };
  var shift = x.shift();
  {
    var __result4 = shift !== 0;
    var __expect4 = false;
  }
  {
    var __result5 = x[0] !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== - 1;
    var __expect6 = false;
  }
  {
    var __result7 = x.length !== 1;
    var __expect7 = false;
  }
  delete x.length;
  {
    var __result8 = x.length !== 2;
    var __expect8 = false;
  }
  