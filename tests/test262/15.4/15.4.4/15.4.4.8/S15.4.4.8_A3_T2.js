  var obj = {
    
  };
  obj.reverse = Array.prototype.reverse;
  obj[0] = "x";
  obj[1] = "y";
  obj[4294967297] = "z";
  obj.length = 4294967298;
  var reverse = obj.reverse();
  {
    var __result1 = reverse !== obj;
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 4294967298;
    var __expect2 = false;
  }
  {
    var __result3 = obj[0] !== "y";
    var __expect3 = false;
  }
  {
    var __result4 = obj[1] !== "x";
    var __expect4 = false;
  }
  {
    var __result5 = obj[4294967297] !== "z";
    var __expect5 = false;
  }
  