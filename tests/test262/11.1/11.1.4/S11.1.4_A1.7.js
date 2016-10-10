  var array = [1, 2, , 4, 5, ];
  {
    var __result1 = typeof array !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = array instanceof Array !== true;
    var __expect2 = false;
  }
  {
    var __result3 = array.toString !== Array.prototype.toString;
    var __expect3 = false;
  }
  {
    var __result4 = array.length !== 5;
    var __expect4 = false;
  }
  {
    var __result5 = array[0] !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = array[1] !== 2;
    var __expect6 = false;
  }
  {
    var __result7 = array[2] !== undefined;
    var __expect7 = false;
  }
  {
    var __result8 = array[3] !== 4;
    var __expect8 = false;
  }
  {
    var __result9 = array[4] !== 5;
    var __expect9 = false;
  }
  