  var obj = {
    
  };
  obj.splice = Array.prototype.splice;
  obj[0] = "x";
  obj[4294967295] = "y";
  obj.length = 4294967296;
  var arr = obj.splice(4294967295, 1);
  {
    var __result1 = arr.length !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = obj[0] !== "x";
    var __expect3 = false;
  }
  {
    var __result4 = obj[4294967295] !== "y";
    var __expect4 = false;
  }
  