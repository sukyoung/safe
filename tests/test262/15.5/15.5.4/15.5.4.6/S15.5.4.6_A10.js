  {
    var __result1 = ! (String.prototype.concat.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.concat.length;
  String.prototype.concat.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.concat.length !== __obj;
    var __expect2 = false;
  }
  