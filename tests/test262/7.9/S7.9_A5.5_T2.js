  var result = (function f(o) 
  {
    o.x = 1;
    return o;
  });
  (new Object()).x;
  {
    var __result1 = typeof result !== "function";
    var __expect1 = false;
  }
  