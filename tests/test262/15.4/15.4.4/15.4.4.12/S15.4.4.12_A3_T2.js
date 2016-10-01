  var obj = {
    
  };
  obj.splice = Array.prototype.splice;
  obj[0] = "x";
  obj.length = 4294967297;
  var arr = obj.splice(0, 1);
  {
    var __result1 = arr.length !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = arr[0] !== "x";
    var __expect2 = false;
  }
  {
    var __result3 = obj.length !== 0;
    var __expect3 = false;
  }
  {
    var __result4 = obj[0] !== undefined;
    var __expect4 = false;
  }
  