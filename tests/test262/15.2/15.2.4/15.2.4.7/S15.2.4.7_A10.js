  {
    var __result1 = ! (Object.prototype.propertyIsEnumerable.hasOwnProperty('length'));
    var __expect1 = false;
  }
  var obj = Object.prototype.propertyIsEnumerable.length;
  Object.prototype.propertyIsEnumerable.length = (function () 
  {
    return "shifted";
  });
  {
    var __result2 = Object.prototype.propertyIsEnumerable.length !== obj;
    var __expect2 = false;
  }
  