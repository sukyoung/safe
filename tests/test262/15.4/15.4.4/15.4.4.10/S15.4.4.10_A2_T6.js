  var obj = {
    
  };
  obj.slice = Array.prototype.slice;
  obj[0] = 0;
  obj[1] = 1;
  obj[2] = 2;
  obj[3] = 3;
  obj[4] = 4;
  obj.length = 5;
  var arr = obj.slice(2, undefined);
  arr.getClass = Object.prototype.toString;
  {
    var __result1 = arr.getClass() !== "[object " + "Array" + "]";
    var __expect1 = false;
  }
  {
    var __result2 = arr.length !== 3;
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
    var __result5 = arr[2] !== 4;
    var __expect5 = false;
  }
  {
    var __result6 = arr[3] !== undefined;
    var __expect6 = false;
  }
  