  var x = [0, 1, 2, 3, 4, ];
  var arr = x.slice(2.5, 4);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr.length !== 2;
    var __expect2 = false;
  }
  {
    var __result3 = arr[0] !== 2;
    var __expect3 = false;
  }
  {
    var __result4 = arr[1] !== 3;
    var __expect4 = false;
  }
  {
    var __result5 = arr[3] !== undefined;
    var __expect5 = false;
  }
  