  var x = [0, 1, ];
  var arr = x.splice(0, - 1);
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
    var __result3 = x.length !== 2;
    var __expect3 = false;
  }
  {
    var __result4 = x[0] !== 0;
    var __expect4 = false;
  }
  {
    var __result5 = x[1] !== 1;
    var __expect5 = false;
  }
  