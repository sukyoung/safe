  var obj = {
    
  };
  obj.slice = Array.prototype.slice;
  obj[0] = "x";
  obj[4294967295] = "y";
  obj.length = 4294967296;
  var arr = obj.slice(0, 4294967296);
  {
    var __result1 = arr.length !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = arr[0] !== undefined;
    var __expect2 = false;
  }
  {
    var __result3 = arr[4294967295] !== undefined;
    var __expect3 = false;
  }
  