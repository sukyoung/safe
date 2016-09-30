  Array.prototype[1] = - 1;
  var x = [0, 1, ];
  var arr = x.splice(1, 1, 2);
  {
    var __result1 = arr.length !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = arr[0] !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = arr[1] !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = x.length !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = x[0] !== 0;
    var __expect5 = false;
  }
  {
    var __result6 = x[1] !== 2;
    var __expect6 = false;
  }
  Object.prototype[1] = - 1;
  Object.prototype.length = 2;
  Object.prototype.splice = Array.prototype.splice;
  x = {
    0 : 0,
    1 : 1
  };
  var arr = x.splice(1, 1, 2);
  {
    var __result7 = arr.length !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = arr[0] !== 1;
    var __expect8 = false;
  }
  {
    var __result9 = arr[1] !== - 1;
    var __expect9 = false;
  }
  {
    var __result10 = x.length !== 2;
    var __expect10 = false;
  }
  {
    var __result11 = x[0] !== 0;
    var __expect11 = false;
  }
  {
    var __result12 = x[1] !== 2;
    var __expect12 = false;
  }
  