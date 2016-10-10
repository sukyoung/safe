  {
    var __result1 = (true || false) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (false || true) !== true;
    var __expect2 = false;
  }
  var x = new Boolean(false);
  {
    var __result3 = (x || true) !== x;
    var __expect3 = false;
  }
  var y = new Boolean(true);
  {
    var __result4 = (false || y) !== y;
    var __expect4 = false;
  }
  var x = new Boolean(false);
  var y = new Boolean(true);
  {
    var __result5 = (x || y) !== x;
    var __expect5 = false;
  }
  var x = false;
  var y = new Boolean(true);
  {
    var __result6 = (x || y) !== y;
    var __expect6 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = false;
  objecty.prop = 1.1;
  {
    var __result7 = (objectx.prop || objecty.prop) !== objecty.prop;
    var __expect7 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = 1.1;
  objecty.prop = false;
  {
    var __result8 = (objectx.prop || objecty.prop) !== objectx.prop;
    var __expect8 = false;
  }
  