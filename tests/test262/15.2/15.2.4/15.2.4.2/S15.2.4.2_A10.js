  {
    var __result1 = ! (Object.prototype.toString.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var obj = Object.prototype.toString.length;
  Object.prototype.toString.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Object.prototype.toString.length !== obj;
    var __expect2 = false;
  }
  