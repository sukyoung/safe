  var x = 0;
  {
    var __result1 = x !== 0;
    var __expect1 = false;
  }
  var y;
  {
    var __result2 = y !== undefined;
    var __expect2 = false;
  }
  var y;
  {
    var __result3 = y !== undefined;
    var __expect3 = false;
  }
  this.y++;
  {
    var __result4 = isNaN(y) !== true;
    var __expect4 = false;
  }
  var string = "/*var y = 0*/";
  {
    var __result5 = string !== "/*var y = 0*/";
    var __expect5 = false;
  }
  var string = "/*var y = 0";
  {
    var __result6 = string !== "/*var y = 0";
    var __expect6 = false;
  }
  