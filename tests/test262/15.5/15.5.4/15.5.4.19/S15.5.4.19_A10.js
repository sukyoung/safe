  {
    var __result1 = ! (String.prototype.toLocaleUpperCase.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.toLocaleUpperCase.length;
  String.prototype.toLocaleUpperCase.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.toLocaleUpperCase.length !== __obj;
    var __expect2 = false;
  }
  