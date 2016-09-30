  Array.prototype[1] = 1;
  var x = [0, ];
  x.length = 2;
  var arr = x.slice();
  {
    var __result1 = arr[0] !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = arr[1] !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = arr.hasOwnProperty('1') !== true;
    var __expect3 = false;
  }
  