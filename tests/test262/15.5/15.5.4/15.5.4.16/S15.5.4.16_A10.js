  {
    var __result1 = ! (String.prototype.toLowerCase.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var __obj = String.prototype.toLowerCase.length;
  String.prototype.toLowerCase.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.toLowerCase.length !== __obj;
    var __expect2 = false;
  }
  