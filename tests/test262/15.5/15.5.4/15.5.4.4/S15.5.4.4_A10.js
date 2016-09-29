  {
    var __result1 = ! (String.prototype.charAt.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.charAt.length;
  String.prototype.charAt.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.charAt.length !== __obj;
    var __expect2 = false;
  }
  