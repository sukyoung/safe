  {
    var __result1 = (false && true) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = (true && false) !== false;
    var __expect2 = false;
  }
  var x = false;
  {
    var __result3 = (x && true) !== false;
    var __expect3 = false;
  }
  var y = new Boolean(false);
  {
    var __result4 = (true && y) !== y;
    var __expect4 = false;
  }
  var x = false;
  var y = true;
  {
    var __result5 = (x && y) !== false;
    var __expect5 = false;
  }
  var x = true;
  var y = new Boolean(false);
  {
    var __result6 = (x && y) !== y;
    var __expect6 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = true;
  objecty.prop = 1.1;
  {
    var __result7 = (objectx.prop && objecty.prop) !== objecty.prop;
    var __expect7 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = 0;
  objecty.prop = true;
  {
    var __result8 = (objectx.prop && objecty.prop) !== objectx.prop;
    var __expect8 = false;
  }
  