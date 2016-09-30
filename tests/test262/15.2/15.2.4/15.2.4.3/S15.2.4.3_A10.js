  {
    var __result1 = ! (Object.prototype.toLocaleString.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var obj = Object.prototype.toLocaleString.length;
  Object.prototype.toLocaleString.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Object.prototype.toLocaleString.length !== obj;
    var __expect2 = false;
  }
  