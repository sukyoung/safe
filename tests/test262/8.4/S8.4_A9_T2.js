  var str = "";
  var strObj = new String("");
  var strObj_ = new String();
  {
    var __result1 = str.constructor !== strObj.constructor;
    var __expect1 = false;
  }
  {
    var __result2 = str.constructor !== strObj_.constructor;
    var __expect2 = false;
  }
  {
    var __result3 = str != strObj;
    var __expect3 = false;
  }
  {
    var __result4 = str === strObj;
    var __expect4 = false;
  }
  {
    var __result5 = str != strObj_;
    var __expect5 = false;
  }
  {
    var __result6 = str === strObj_;
    var __expect6 = false;
  }
  