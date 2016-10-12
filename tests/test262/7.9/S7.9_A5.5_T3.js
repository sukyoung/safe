  var result = (function f(o) 
  {
    o.x = 1;
    return o;
  })(new Object()).x;
  {
    var __result1 = result !== 1;
    var __expect1 = false;
  }
  