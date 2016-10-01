  var obj = {
    
  };
  obj.join = Array.prototype.join;
  obj[0] = "x";
  obj[1] = "y";
  obj[4294967296] = "z";
  obj.length = 4294967297;
  {
    var __result1 = obj.join("") !== "x";
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 4294967297;
    var __expect2 = false;
  }
  