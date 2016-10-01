  {
    var __result1 = ! (String.hasOwnProperty('prototype'));
    var __expect1 = false;
  }
  var __obj = String.prototype;
  String.prototype = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype !== __obj;
    var __expect2 = false;
  }
  