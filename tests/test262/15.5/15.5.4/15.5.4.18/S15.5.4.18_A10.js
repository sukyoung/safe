  {
    var __result1 = ! (String.prototype.toUpperCase.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.toUpperCase.length;
  String.prototype.toUpperCase.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.toUpperCase.length !== __obj;
    var __expect2 = false;
  }
  