  {
    var __result1 = ! (String.prototype.slice.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.slice.length;
  String.prototype.slice.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.slice.length !== __obj;
    var __expect2 = false;
  }
  