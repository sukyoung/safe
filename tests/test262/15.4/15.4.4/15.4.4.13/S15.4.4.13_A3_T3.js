  var obj = {
    
  };
  obj.unshift = Array.prototype.unshift;
  obj[0] = "";
  obj.length = 4294967297;
  var unshift = obj.unshift("x", "y", "z");
  {
    var __result1 = unshift !== 4;
    var __expect1 = false;
  }
  {
    var __result2 = obj.length !== 4;
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
  {
    var __result6 = obj[3] !== "";
    var __expect6 = false;
  }
  