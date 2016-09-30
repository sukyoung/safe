  var x = [0, 1, 2, 3, 4, ];
  var arr = x.slice(- 6, - 6);
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
    var __result3 = arr[0] !== undefined;
    var __expect3 = false;
  }
  