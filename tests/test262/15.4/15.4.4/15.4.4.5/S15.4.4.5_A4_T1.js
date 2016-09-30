  var obj = {
    
  };
  obj.join = Array.prototype.join;
  obj[0] = "x";
  obj[4294967295] = "y";
  obj.length = 4294967296;
  {
    var __result1 = obj.join("") !== "";
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 4294967296;
    var __expect2 = false;
  }
  