  {
    var __result1 = ! (String.prototype.lastIndexOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.lastIndexOf.length;
  String.prototype.lastIndexOf.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.lastIndexOf.length !== __obj;
    var __expect2 = false;
  }
  