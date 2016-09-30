  var x = [0, 1, 2, 3, ];
  var arr = x.splice(Number.POSITIVE_INFINITY, 3);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr.length !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = x[0] !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = x[1] !== 1;
    var __expect4 = false;
  }
  {
    var __result5 = x[2] !== 2;
    var __expect5 = false;
  }
  {
    var __result6 = x[3] !== 3;
    var __expect6 = false;
  }
  