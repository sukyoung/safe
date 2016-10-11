  var str = 'ABC';
  var strObj = new String('ABC');
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
  