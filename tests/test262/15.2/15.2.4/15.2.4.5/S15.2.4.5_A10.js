  {
    var __result1 = ! (Object.prototype.hasOwnProperty.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var obj = Object.prototype.hasOwnProperty.length;
  Object.prototype.hasOwnProperty.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Object.prototype.hasOwnProperty.length !== obj;
    var __expect2 = false;
  }
  