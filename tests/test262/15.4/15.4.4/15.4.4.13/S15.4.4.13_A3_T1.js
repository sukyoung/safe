  var obj = {
    
  };
  obj.unshift = Array.prototype.unshift;
  obj.length = 4294967296;
  var unshift = obj.unshift("x", "y", "z");
  {
    var __result1 = unshift !== 3;
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 3;
    var __expect2 = false;
  }
  {
    var __result3 = obj[0] !== "x";
    var __expect3 = false;
  }
  {
    var __result4 = obj[1] !== "y";
    var __expect4 = false;
  }
  {
    var __result5 = obj[2] !== "z";
    var __expect5 = false;
  }
  var obj = {
    
  };
  obj.unshift = Array.prototype.unshift;
  obj.length = 4294967296;
  var unshift = obj.unshift();
  {
    var __result6 = unshift !== 0;
    var __expect6 = false;
  }
  {
    var __result7 = obj.length !== 0;
    var __expect7 = false;
  }
  