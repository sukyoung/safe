  var __old__Array__prototype__toString = Array.prototype.toString;
  Array.prototype.toString = (function () 
  {
    return "__ARRAY__";
  });
  var __str = String(new Array);
  Array.prototype.toString = __old__Array__prototype__toString;
  {
    var __result1 = typeof __str !== "string";
    var __expect1 = false;
  }
  {
    var __result2 = __str !== "__ARRAY__";
    var __expect2 = false;
  }
  