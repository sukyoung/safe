  var obj = {
    0 : 0,
    1 : 1
  };
  obj.length = 2;
  obj.splice = Array.prototype.splice;
  var arr = obj.splice(- 2, - 1, 2, 3);
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
    var __result3 = obj.length !== 4;
    var __expect3 = false;
  }
  {
    var __result4 = obj[0] !== 2;
    var __expect4 = false;
  }
  {
    var __result5 = obj[1] !== 3;
    var __expect5 = false;
  }
  {
    var __result6 = obj[2] !== 0;
    var __expect6 = false;
  }
  {
    var __result7 = obj[3] !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = obj[4] !== undefined;
    var __expect8 = false;
  }
  