  Array.prototype[1] = 1;
  var x = [0, ];
  x.length = 2;
  var arr = x.concat();
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
  Object.prototype[1] = 1;
  Object.prototype.length = 2;
  Object.prototype.concat = Array.prototype.concat;
  x = {
    0 : 0
  };
  var arr = x.concat();
  {
    var __result4 = arr[0] !== x;
    var __expect4 = false;
  }
  {
    var __result5 = arr[1] !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = arr.hasOwnProperty('1') !== false;
    var __expect6 = false;
  }
  
