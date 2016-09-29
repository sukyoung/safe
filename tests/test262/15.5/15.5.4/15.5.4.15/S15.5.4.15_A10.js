  {
    var __result1 = ! (String.prototype.substring.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.substring.length;
  String.prototype.substring.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.substring.length !== __obj;
    var __expect2 = false;
  }
  