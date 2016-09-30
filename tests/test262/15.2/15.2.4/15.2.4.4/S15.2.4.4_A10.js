  {
    var __result1 = ! (Object.prototype.valueOf.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var obj = Object.prototype.valueOf.length;
  Object.prototype.valueOf.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Object.prototype.valueOf.length !== obj;
    var __expect2 = false;
  }
  