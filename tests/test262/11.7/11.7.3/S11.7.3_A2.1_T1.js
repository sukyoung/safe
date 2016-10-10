  {
    var __result1 = - 4 >>> 1 !== 2147483646;
    var __expect1 = false;
  }
  var x = - 4;
  {
    var __result2 = x >>> 1 !== 2147483646;
    var __expect2 = false;
  }
  var y = 1;
  {
    var __result3 = - 4 >>> y !== 2147483646;
    var __expect3 = false;
  }
  var x = - 4;
  var y = 1;
  {
    var __result4 = x >>> y !== 2147483646;
    var __expect4 = false;
  }
  var objectx = new Object();
  var objecty = new Object();
  objectx.prop = - 4;
  objecty.prop = 1;
  {
    var __result5 = objectx.prop >>> objecty.prop !== 2147483646;
    var __expect5 = false;
  }
  