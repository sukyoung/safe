  var str = "";
  var strObj = new String;
  {
    var __result1 = str.constructor !== strObj.constructor;
    var __expect1 = false;
  }
  {
    var __result2 = str != strObj;
    var __expect2 = false;
  }
  {
    var __result3 = str === strObj;
    var __expect3 = false;
  }
  {
    var __result4 = typeof str == typeof strObj;
    var __expect4 = false;
  }
  