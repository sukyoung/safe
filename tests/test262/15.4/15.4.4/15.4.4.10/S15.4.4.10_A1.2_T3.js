  var x = [0, 1, 2, 3, 4, ];
  var arr = x.slice(- 5, 1);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr.length !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = arr[0] !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = arr[1] !== undefined;
    var __expect4 = false;
  }
  