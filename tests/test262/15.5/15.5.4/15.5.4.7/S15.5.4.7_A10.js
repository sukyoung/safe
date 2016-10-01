  {
    var __result1 = ! (String.prototype.indexOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.indexOf.length;
  String.prototype.indexOf.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.indexOf.length !== __obj;
    var __expect2 = false;
  }
  