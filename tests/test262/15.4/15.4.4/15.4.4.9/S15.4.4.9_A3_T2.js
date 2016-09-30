  var obj = {
    
  };
  obj.shift = Array.prototype.shift;
  obj[0] = "x";
  obj[1] = "y";
  obj[4294967296] = "z";
  obj.length = 4294967297;
  var shift = obj.shift();
  {
    var __result1 = shift !== "x";
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = obj[0] !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = obj[1] !== "y";
    var __expect4 = false;
  }
  {
    var __result5 = obj[4294967296] !== "z";
    var __expect5 = false;
  }
  