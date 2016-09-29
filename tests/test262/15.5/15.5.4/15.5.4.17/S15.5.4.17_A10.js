  {
    var __result1 = ! (String.prototype.toLocaleLowerCase.hasOwnProperty('length'));
    var __expect1 = false;
  }
  __obj = String.prototype.toLocaleLowerCase.length;
  String.prototype.toLocaleLowerCase.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = String.prototype.toLocaleLowerCase.length !== __obj;
    var __expect2 = false;
  }
  