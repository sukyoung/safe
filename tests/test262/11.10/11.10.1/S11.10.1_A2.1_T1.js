  {
    var __result1 = (1 & 1) !== 1;
    var __expect1 = false;
  }
  var x = 1;
  {
    var __result2 = (x & 1) !== 1;
    var __expect2 = false;
  }
  var y = 1;
  {
    var __result3 = (1 & y) !== 1;
    var __expect3 = false;
  }
  var x = 1;
  var y = 1;
  {
    var __result4 = (x & y) !== 1;
    var __expect4 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = 1;
  objecty.prop = 1;
  {
    var __result5 = (objectx.prop & objecty.prop) !== 1;
    var __expect5 = false;
  }
  