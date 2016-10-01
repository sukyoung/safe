  var x = [0, 1, 2, 3, 4, ];
  var arr = x.slice(- 9, - 1);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr.length !== 4;
    var __expect2 = false;
  }
  {
    var __result3 = arr[0] !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = arr[1] !== 1;
    var __expect4 = false;
  }
  {
    var __result5 = arr[2] !== 2;
    var __expect5 = false;
  }
  {
    var __result6 = arr[3] !== 3;
    var __expect6 = false;
  }
  {
    var __result7 = arr[4] !== undefined;
    var __expect7 = false;
  }
  