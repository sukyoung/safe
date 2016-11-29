  var obj = {
    
  };
  obj.sort = Array.prototype.sort;
  obj[0] = "z";
  obj[1] = "y";
/*
  obj[4294967297] = "x";
  obj.length = 4294967298;
*/
  {
    var __result1 = obj.sort() !== obj;
    var __expect1 = false;
  }
/*
  {
    var __result2 = obj.length !== 4294967298;
    var __expect2 = false;
  }
  {
    var __result3 = obj[0] !== "y";
    var __expect3 = false;
  }
  {
    var __result4 = obj[1] !== "z";
    var __expect4 = false;
  }
  {
    var __result5 = obj[4294967297] !== "x";
    var __expect5 = false;
  }
  */
